package explode2.booster.graphql

import com.expediagroup.graphql.generator.extensions.print
import explode2.booster.*
import explode2.booster.event.KtorInitEvent
import explode2.booster.event.KtorModuleEvent
import explode2.logging.Colors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("GraphQL")

class GraphQLPlugin : BoosterPlugin {

	override val id: String = "graphql"
	override val version: String = "1.0.0"

	private val server = GraphQLServer<ApplicationCall>(
		MazeProvider.getProvider().query,
		MazeProvider.getProvider().mutation,
		{ it.receiveText() },
		{ mapOf("token" to (it.request.header("x-soudayo") ?: "trash-potato-server")) }
	)

	override fun onPreInit() {
		// output Labyrinth provider info
		logger.info("Using Labyrinth: ${Colors.TianYi}${Booster.labyrinth.javaClass.canonicalName}")

		// output Maze provider info
		logger.info("Using Maze: ${Colors.TianYi}${MazeProvider.getProvider().javaClass.canonicalName}")

		// register subscriber
		subscribeEvents()
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onKtorInit(e: KtorInitEvent) {
		logger.info("Validating Ktor address binding")
		e.newThread = false
	}

	@Subscribe
	fun onKtorModule(e: KtorModuleEvent) {
		logger.info("Configuring GraphQL paths in Ktor")

		val welcome = listOf("❄", "❤", "\uD83D\uDCE2", "\uD83D\uDCE3")
		val playground = Application::class.java.classLoader.getResource("graphql-playground/index.html")?.readText() ?: "<p>Resource Not Found!</p>"

		e.configure {
			routing {
				route("graphql") {
					get {
						call.respondText(welcome.random())
					}
					post {
						when(val r = server.handle(call)) {
							null -> call.respondText("Invalid request!", status = HttpStatusCode.BadRequest)
							else -> call.respondText(r)
						}
					}
				}

				get("sdl") {
					call.respondText(server.getSchema().print())
				}

				get("playground") {
					call.respondText(playground, ContentType.Text.Html)
				}
			}
		}
	}
}