package explode2.util.simplegraphql

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

data class GraphqlRequestBody(val query: String, val variables: Map<String, Any>, val operationName: String?)

suspend inline fun <reified T> HttpClient.graphql(
	url: String,
	query: String,
	variables: Map<String, Any> = mapOf(),
	operationName: String? = null,
	soudayo: String? = null,
	block: HttpRequestBuilder.() -> Unit = {}
): T = post(url) {
	contentType(ContentType.Application.Json)
	setBody(GraphqlRequestBody(query, variables, operationName))

	// apply tokens
	if(soudayo != null) soudayo(soudayo)

	apply(block)
}.body()

fun HttpRequestBuilder.soudayo(token: String) = header("x-soudayo", token)