package explode2.booster.event

import io.ktor.server.application.*
import io.ktor.server.routing.*

class KtorModuleEvent(private val application: Application) {

	fun configure(configure: ApplicationConfigure) = apply { application.configure() }

}

typealias ApplicationConfigure = Application.() -> Unit
typealias RouteConfigure = Route.() -> Unit
