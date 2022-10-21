package explode2.booster.bomb.submods.user

import explode2.booster.bomb.*
import explode2.booster.bomb.bombCall
import explode2.booster.bomb.submods.*
import explode2.booster.event.RouteConfigure
import io.ktor.server.auth.*
import io.ktor.server.routing.*

internal val userModule: RouteConfigure = {

	authenticate {
		get("me") {
			val u = bombCall.principal<BombPrincipal>()?.user
			if(u != null) {
				bombCall.respondData(u.toBO().toData())
			} else {
				bombCall.respondError(toError(Localization.NotAuthenticatedOrUserNotFound))
			}
		}
	}

}