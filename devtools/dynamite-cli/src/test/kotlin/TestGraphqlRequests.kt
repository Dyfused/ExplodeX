import explode2.dynamite.DynamiteRequests
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal class TestGraphqlRequests {

	private val http = HttpClient(OkHttp) {
		install(ContentNegotiation) { gson() }
	}

	private val dr = DynamiteRequests("http://localhost:10443/graphql", "3400cba2-cd86-43d3-ace4-0da1a92481c9", http)

	@Test
	fun `test hello`() = runBlocking {
		println(dr.hello())
	}

	@Test
	fun `test reviewSet`() = runBlocking { // FIXME: 看起来好像 Graphql 模块本身就有问题，抽空调查一下
		println(dr.reviewSets(10, 0, ""))
	}

	@Test
	fun `test gotSet`() = runBlocking {
		println(dr.gotSet())
	}

	@Test
	@Suppress("BooleanLiteralArgument")
	fun `test fetchSets`(): Unit = runBlocking {
		val flow = flow {
			var index = 0
			// ranked
			while(true) {
				val d = dr.fetchSets(0, -1, 9, index, false, "", false, true)
				if(d.isNotEmpty() && ((d["data"] as Map<*, *>)["r"] as List<*>).isNotEmpty()) {
					emit(d)
					index += 9
				} else {
					break
				}
			}
			// unranked
			while(true) {
				val d = dr.fetchSets(0, -1, 9, index, false, "", false, false)
				if(d.isNotEmpty() && ((d["data"] as Map<*, *>)["r"] as List<*>).isNotEmpty()) {
					emit(d)
					index += 9
				} else {
					break
				}
			}
		}

		flow.collectIndexed { index, value ->
			println("[$index] $value")
		}
	}

}