package explode2.booster.resource

import explode2.booster.BoosterPlugin
import explode2.booster.event.KtorModuleEvent
import explode2.booster.subscribeEvents
import explode2.labyrinth.LabyrinthPlugin
import explode2.logging.Colors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.greenrobot.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.io.File
import java.util.ServiceLoader

internal val logger = LoggerFactory.getLogger("Resource")

class ResourcePlugin : BoosterPlugin {

	override val id: String = "resource"
	override val version: String = "1.0"

	override fun onPreInit() {
		subscribeEvents()
	}

	@Subscribe
	fun registerKtor(e: KtorModuleEvent) {
		val srv = ServiceLoader.load(ResourceProvider::class.java).findFirst().orElseGet { FileSystemProvider(File(".explode_data")) }
		logger.info("Using Resource: ${Colors.TianYi}${srv.javaClass.canonicalName}")

		logger.info("Configuring Ktor")

		suspend fun PipelineContext<Unit, ApplicationCall>.invalidResp(msg: String = "Invalid") =
			call.respondText(msg, status = HttpStatusCode.BadRequest)

		e.configure {
			routing {
				route("download") {
					get("/music/encoded/{sid}") {
						val sid = call.parameters["sid"] ?: return@get invalidResp()
						call.respondBytes(srv.getMusic(sid))
					}
					get("/preview/encoded/{sid}") {
						val sid = call.parameters["sid"] ?: return@get invalidResp()
						call.respondBytes(srv.getPreviewMusic(sid))
					}
					get("/cover/encoded/{sid}") {
						val sid = call.parameters["sid"] ?: return@get invalidResp()
						call.respondBytes(srv.getCoverPicture(sid))
					}
					get("/chart/encoded/{cid}") {
						val cid = call.parameters["cid"] ?: return@get invalidResp()
						val sid = LabyrinthPlugin.labyrinth.songSetFactory.getSongSetByChart(cid)?.id ?: return@get invalidResp("Invalid Binding Set")
						call.respondBytes(srv.getChartXML(cid, sid))
					}
					get("/cover/480x270_jpg/{sid}") {
						val sid = call.parameters["sid"] ?: return@get invalidResp()
						call.respondBytes(srv.getStoreCoverPicture(sid))
					}
					get("/avatar/256x256_jpg/{uid}") {
						val uid = call.parameters["uid"] ?: return@get invalidResp()
						call.respondBytes(srv.getUserAvatar(uid))
					}
				}
			}
		}
	}
}