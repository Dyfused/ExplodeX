package explode2.booster

import com.electronwill.nightconfig.core.file.FileConfig
import kotlin.io.path.Path

interface BoosterPlugin {
	val id: String
	val version: String

	fun onInit() {}
	fun onPreInit() {}
	fun onPostInit() {}
}

fun <T> T.subscribeEvents() {
	Booster.eventbus.register(this)
}

val BoosterPlugin.config: FileConfig
	get() = FileConfig.builder(Path("$id.config.toml")).autoreload().autosave().charset(Charsets.UTF_8).build()