package explode2.booster.event

import io.ktor.server.application.*

class KtorModuleEvent(private val application: Application) {

	fun configure(configure: Application.() -> Unit) = apply { application.configure() }

}
