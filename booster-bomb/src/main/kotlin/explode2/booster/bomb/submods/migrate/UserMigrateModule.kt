package explode2.booster.bomb.submods.migrate

import explode2.booster.bomb.Localization
import explode2.booster.bomb.bombCall
import explode2.booster.bomb.respondData
import explode2.booster.bomb.respondError
import explode2.booster.bomb.submods.BombPrincipal
import explode2.booster.bomb.submods.toData
import explode2.booster.bomb.submods.toError
import explode2.booster.bomb.submods.user.toBO
import explode2.booster.event.RouteConfigure
import explode2.gateau.GameRecord
import io.ktor.server.auth.*
import io.ktor.server.routing.*

// [/migrate]
internal val migrationModule: RouteConfigure = {

	authenticate {
		post {
			val user = bombCall.principal<BombPrincipal>()?.user
				?: return@post bombCall.respondError(toError(Localization.NotAuthenticatedOrUserNotFound))

			val skip = bombCall.parameters["skip"]?.toIntOrNull() ?: 0
			val limit = bombCall.parameters["limit"]?.toIntOrNull() ?: 100

			val recs = user.getAllRecords(limit, skip)

			bombCall.respondData(UserMigrationBO(user.toBO(), recs.map(GameRecord::toBO)).toData())
		}
	}

}