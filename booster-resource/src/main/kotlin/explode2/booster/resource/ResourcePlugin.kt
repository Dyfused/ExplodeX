package explode2.booster.resource

import explode2.booster.BoosterPlugin
import explode2.booster.ExplodeService
import explode2.booster.event.KtorModuleEvent
import explode2.booster.subscribeEvents
import explode2.labyrinth.LabyrinthPlugin
import explode2.logging.Colors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.greenrobot.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.io.File

internal val logger = LoggerFactory.getLogger("Resource")

class ResourcePlugin : BoosterPlugin {

	override val id: String = "resource"
	override val version: String = "1.0.0"

	lateinit var service: ResourceProvider

	override fun onPreInit() {
		this.service = ExplodeService.load<ResourceProvider>().findFirst()
			.orElseGet { FileSystemProvider(File(".explode_data")) }
		subscribeEvents()
	}

	@Subscribe
	fun registerKtor(e: KtorModuleEvent) {
		val srv = service
		logger.info("Using Resource: ${Colors.TianYi}${srv.javaClass.canonicalName}")

		logger.info("Configuring Ktor")

		suspend fun PipelineContext<Unit, ApplicationCall>.invalidResp(msg: String = "Invalid") =
			call.respondText(msg, status = HttpStatusCode.BadRequest)

		suspend fun PipelineContext<Unit, ApplicationCall>.unsupported(msg: String = "Unsupported action") =
			call.respondText(msg, status = HttpStatusCode.MethodNotAllowed)

		suspend fun PipelineContext<Unit, ApplicationCall>.done() =
			call.respond(HttpStatusCode.NoContent)

		e.configure {
			routing {
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

							when (srv) {
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

							when (srv) {
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
							val sid = LabyrinthPlugin.labyrinth.songSetFactory.getSongSetByChart(cid)?.id
								?: return@get invalidResp("Invalid binding set")
							val token = call.request.header("x-soudayo")

							when (srv) {
								is RedirectResourceProvider -> call.respondRedirect(srv.getChartXML(cid, sid, token))
								is ByteArrayResourceProvider -> call.respondBytes(srv.getChartXML(cid, sid, token))
							}
						}
						post {
							val cid = call.parameters["cid"] ?: return@post invalidResp()
							val sid = LabyrinthPlugin.labyrinth.songSetFactory.getSongSetByChart(cid)?.id
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

							when (srv) {
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

							when (srv) {
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
}