package explode2.simplegql

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object SimpleGraphQL {

	val client = HttpClient(CIO)

	val objectMapper: ObjectMapper = ObjectMapper().registerModule(
		KotlinModule.Builder()
			.withReflectionCacheSize(512)
			.configure(KotlinFeature.NullToEmptyCollection, true)
			.configure(KotlinFeature.NullToEmptyMap, true)
			.configure(KotlinFeature.NullIsSameAsDefault, false)
			.configure(KotlinFeature.SingletonSupport, false)
			.configure(KotlinFeature.StrictNullChecks, false)
			.build()
	).registerModule(JavaTimeModule())

	suspend inline fun <reified T> send(url: String, request: SingleQuery, token: String? = null): T {
		return send(Url(url), request, token)
	}

	suspend inline fun <reified T> send(url: Url, request: SingleQuery, token: String? = null): T {
		return client.post(url) {
			setBody(request.toJson())
			header("x-soudayo", token)
		}.body()
	}

	suspend inline fun <reified T> SingleQuery.send(url: String, token: String? = null) =
		send<T>(url, this, token)


	suspend inline fun <reified T> HttpResponse.body(): T {
		return objectMapper.readValue(bodyAsText())
	}
}