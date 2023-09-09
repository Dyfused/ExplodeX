package explode2.booster

import com.expediagroup.graphql.generator.extensions.print
import explode2.booster.graphql.MazeProvider
import explode2.booster.graphql.graphQLServer
import explode2.booster.resource.ByteArrayResourceProvider
import explode2.booster.resource.RedirectResourceProvider
import explode2.booster.resource.ResourceReceiver
import explode2.labyrinth.SongSetRepository
import explode2.logging.Colors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MainLogics : KoinComponent {

	private val gqlLogger: Logger = LoggerFactory.getLogger("GraphQL")
	private val resLogger: Logger = LoggerFactory.getLogger("Resource")

	fun setupGraphQL(app: Application) {
		// output Labyrinth provider info
		gqlLogger.info("Using Labyrinth: ${Colors.TianYi}${Explode.labyrinth.javaClass.canonicalName}")

		// output Maze provider info
		gqlLogger.info("Using Maze: ${Colors.TianYi}${MazeProvider.getProvider().javaClass.canonicalName}")

		val welcome = listOf("❄", "❤", "\uD83D\uDCE2", "\uD83D\uDCE3")
		val playground = Application::class.java.classLoader.getResource("graphql-playground/index.html")?.readText()
			?.replace(
				"\$BACKEND_URL\$",
				"//127.0.0.1:10443/graphql"
			)
			?: "<p>Resource Not Found!</p>"

		app.routing {
			route("graphql") {
				get {
					call.respondText(welcome.random())
				}
				post {
					when(val r = graphQLServer.handle(call)) {
						null -> call.respondText(
							"Invalid request!",
							contentType = ContentType.Application.Json,
							status = HttpStatusCode.BadRequest
						)

						else -> call.respondText(r, contentType = ContentType.Application.Json)
					}
				}
			}

			get("sdl") {
				call.respondText(graphQLServer.getSchema().print())
			}

			get("playground") {
				call.respondText(playground, ContentType.Text.Html)
			}
		}
	}

	private val songRepo by inject<SongSetRepository>()

	fun setupResource(app: Application) {
		val srv = Explode.resource

		resLogger.info("Using Resource: ${Colors.TianYi}${srv.javaClass.canonicalName}")

		suspend fun PipelineContext<Unit, ApplicationCall>.invalidResp(msg: String = "Invalid") =
			call.respondText(msg, status = HttpStatusCode.BadRequest)

		suspend fun PipelineContext<Unit, ApplicationCall>.unsupported(msg: String = "Unsupported action") =
			call.respondText(msg, status = HttpStatusCode.MethodNotAllowed)

		suspend fun PipelineContext<Unit, ApplicationCall>.done() =
			call.respond(HttpStatusCode.NoContent)

		app.routing {
			route("download") {
				route("/music/encoded/{sid}") {
					get {
						val sid = call.parameters["sid"] ?: return@get invalidResp()
						val token = call.request.header("x-soudayo")

						when(srv) {
							is RedirectResourceProvider -> call.respondRedirect(srv.getMusic(sid, token))
							is ByteArrayResourceProvider -> call.respondBytes(srv.getMusic(sid, token))
						}
					}
					post {
						val sid = call.parameters["sid"] ?: return@post invalidResp()
						val token = call.request.header("x-soudayo")

						if(srv is ResourceReceiver) {
							srv.uploadMusic(sid, token, call.receiveStream().readBytes())
							return@post done()
						} else {
							return@post unsupported()
						}
					}
				}
				route("/preview/encoded/{sid}") {
					get {
						val sid = call.parameters["sid"] ?: return@get invalidResp()
						val token = call.request.header("x-soudayo")

						when(srv) {
							is RedirectResourceProvider -> call.respondRedirect(srv.getPreviewMusic(sid, token))
							is ByteArrayResourceProvider -> call.respondBytes(srv.getPreviewMusic(sid, token))
						}
					}
					post {
						val sid = call.parameters["sid"] ?: return@post invalidResp()
						val token = call.request.header("x-soudayo")

						if(srv is ResourceReceiver) {
							srv.uploadPreviewMusic(sid, token, call.receiveStream().readBytes())
							return@post done()
						} else {
							return@post unsupported()
						}
					}
				}
				route("/cover/encoded/{sid}") {
					get {
						val sid = call.parameters["sid"] ?: return@get invalidResp()
						val token = call.request.header("x-soudayo")

						when(srv) {
							is RedirectResourceProvider -> call.respondRedirect(srv.getCoverPicture(sid, token))
							is ByteArrayResourceProvider -> call.respondBytes(srv.getCoverPicture(sid, token))
						}
					}
					post {
						val sid = call.parameters["sid"] ?: return@post invalidResp()
						val token = call.request.header("x-soudayo")

						if(srv is ResourceReceiver) {
							srv.uploadCoverPicture(sid, token, call.receiveStream().readBytes())
							return@post done()
						} else {
							return@post unsupported()
						}
					}
				}
				route("/chart/encoded/{cid}") {
					get {
						val cid = call.parameters["cid"] ?: return@get invalidResp()
						val sid = songRepo.getSongSetByChart(cid)?.id
							?: return@get invalidResp("Invalid binding set")
						val token = call.request.header("x-soudayo")

						when(srv) {
							is RedirectResourceProvider -> call.respondRedirect(srv.getChartXML(cid, sid, token))
							is ByteArrayResourceProvider -> call.respondBytes(srv.getChartXML(cid, sid, token))
						}
					}
					post {
						val cid = call.parameters["cid"] ?: return@post invalidResp()
						val sid = songRepo.getSongSetByChart(cid)?.id
							?: return@post invalidResp()
						val token = call.request.header("x-soudayo")

						if(srv is ResourceReceiver) {
							srv.uploadChartXML(cid, sid, token, call.receiveStream().readBytes())
							return@post done()
						} else {
							return@post unsupported()
						}
					}
				}
				route("/cover/480x270_jpg/{sid}") {
					get {
						val sid = call.parameters["sid"] ?: return@get invalidResp()
						val token = call.request.header("x-soudayo")

						when(srv) {
							is RedirectResourceProvider -> call.respondRedirect(srv.getStoreCoverPicture(sid, token))
							is ByteArrayResourceProvider -> call.respondBytes(srv.getStoreCoverPicture(sid, token))
						}
					}
					post {
						val sid = call.parameters["sid"] ?: return@post invalidResp()
						val token = call.request.header("x-soudayo")

						if(srv is ResourceReceiver) {
							srv.uploadStoreCoverPicture(sid, token, call.receiveStream().readBytes())
							return@post done()
						} else {
							return@post unsupported()
						}
					}
				}
				route("/avatar/256x256_jpg/{uid}") {
					get {
						val uid = call.parameters["uid"] ?: return@get invalidResp()
						val token = call.request.header("x-soudayo")

						when(srv) {
							is RedirectResourceProvider -> call.respondRedirect(srv.getUserAvatar(uid, token))
							is ByteArrayResourceProvider -> call.respondBytes(srv.getUserAvatar(uid, token))
						}
					}
					post {
						val uid = call.parameters["uid"] ?: return@post invalidResp()
						val token = call.request.header("x-soudayo")

						if(srv is ResourceReceiver) {
							srv.uploadUserAvatar(uid, token, call.receiveStream().readBytes())
							return@post done()
						} else {
							return@post unsupported()
						}
					}
				}
			}
		}
	}

}