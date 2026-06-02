package explode2.booster.maintain

import explode2.booster.BoosterPlugin
import explode2.booster.event.KtorModuleEvent
import explode2.booster.subscribeEvents
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.greenrobot.eventbus.Subscribe

class MaintainPlugin : BoosterPlugin {

	override val id: String = "maintain"
	override val version: String = "1.0.1"

	init {
		subscribeEvents()
	}

	@Subscribe
	fun onKtorModule(e: KtorModuleEvent) = e.configure {
		val resp = listOf("Maintain 7716 is now not working at server.",
			"Welcome to Dynamite Explode!")

		routing {
			get("/") {
				call.respondText(resp.random())
			}
		}
	}
}