@file:Suppress("DataClassPrivateConstructor")

package explode2.simplegql

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode

private val mapper = ObjectMapper()

data class SingleQuery private constructor(
	val query: String,
	val variables: MutableMap<String, JsonNode>,
	val operationName: String?
) {

	fun variable(key: String, value: String) = apply {
		variables[key] = TextNode(value)
	}

	fun toJson(): String {
		return mapper.writeValueAsString(this)
	}

	companion object {
		fun SingleQuery(query: String, variables: MutableMap<String, JsonNode> = mutableMapOf()) =
			SingleQuery(query.replace("!", "$"), variables, null)
	}
}