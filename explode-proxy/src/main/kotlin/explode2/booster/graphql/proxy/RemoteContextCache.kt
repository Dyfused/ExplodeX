package explode2.booster.graphql.proxy

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

class RemoteContextCache(private val file: File, private val original: MutableMap<String, RemoteContext> = mutableMapOf()) :
	MutableMap<String, RemoteContext> by original {

	override fun put(key: String, value: RemoteContext): RemoteContext? {
		return original.put(key, value).saveThis()
	}

	override fun remove(key: String): RemoteContext? {
		return original.remove(key).saveThis()
	}

	init {
		readThis()
	}

	fun load() {
		readThis()
	}

	fun save() {
		saveThis()
	}

	private fun <T> T.saveThis() = apply {
		file.writeText(jacksonObjectMapper().writeValueAsString(original), Charsets.UTF_8)
	}

	private fun readThis() {
		if(file.exists()) {
			original.clear()
			original.putAll(jacksonObjectMapper().readValue(file.readText(Charsets.UTF_8), object : TypeReference<LinkedHashMap<String, RemoteContext>>() {}))
		}
	}
}