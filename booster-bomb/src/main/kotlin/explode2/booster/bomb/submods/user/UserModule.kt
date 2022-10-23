package explode2.booster.bomb.submods.user

import explode2.booster.bomb.*
import explode2.booster.bomb.bombCall
import explode2.booster.bomb.submods.*
import explode2.booster.event.RouteConfigure
import explode2.gateau.GameRecord
import explode2.gateau.ScoreOrRanking
import explode2.labyrinth.LabyrinthPlugin.Companion.labyrinth
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

// [/user]
internal val userModule: RouteConfigure = {

	authenticate {
		// [/user/me]
		get("me") {
			val user = bombCall.principal<BombPrincipal>()?.user
			if(user != null) {
				bombCall.respondData(user.toBO().toData())
			} else {
				bombCall.respondError(toError(Localization.NotAuthenticatedOrUserNotFound))
			}
		}
	}

	// [/user/{id}]
	get("{id}") {
		val uid = bombCall.parameters.getOrFail("id")
		val user = labyrinth.gameUserFactory.getGameUserById(uid)
		if(user != null) {
			bombCall.respondData(user.toBO().toData())
		} else {
			bombCall.respondError(toError(Localization.UserNotFound))
		}
	}

	// [/user/register]
	post("register") {
		data class RegisterPayload(val username: String, val password: String)

		val payload = bombCall.receive<RegisterPayload>()
		val username = payload.username
		val password = payload.password

		if(labyrinth.gameUserFactory.getGameUserByName(username) != null) {
			bombCall.respondError(toError(Localization.UserAlreadyExists))
		} else {
			val user = labyrinth.gameUserFactory.createGameUser(username, password)
			bombCall.respondData(user.toBO().toData())
		}
	}

	route("{id}") {

		// [/user/{id}/best]
		get("best") {
			val uid = bombCall.parameters.getOrFail("id")

			// 请求长度，默认为 20
			val limit = bombCall.parameters["limit"]?.toIntOrNull()?.clamp(1..100) ?: 20
			// 只有参数【?by=score】才会使用分数顺序
			val scoreMode = bombCall.parameters["by"] == "score"

			val user = labyrinth.gameUserFactory.getGameUserById(uid)
				?: return@get bombCall.respondError(toError(Localization.UserNotFound))

			// 成绩列表
			val list = user.calculateBestRecords(limit, if(scoreMode) ScoreOrRanking.Score else ScoreOrRanking.Ranking).map(GameRecord::toBO)

			bombCall.respondData(list.toData())
		}

		// [/user/{id}/last]
		get("last") {
			val uid = bombCall.parameters.getOrFail("id")

			// 请求长度，默认为 20
			val limit = bombCall.parameters["limit"]?.toIntOrNull()?.clamp(1..100) ?: 20

			val user = labyrinth.gameUserFactory.getGameUserById(uid)
				?: return@get bombCall.respondError(toError(Localization.UserNotFound))

			// 成绩列表
			val list = user.calculateLastRecords(limit).map(GameRecord::toBO)

			bombCall.respondData(list.toData())
		}

		// [/user/{id}/username]
		patch("username") {
			val uid = bombCall.parameters.getOrFail("id")

			data class ChangeUsernamePayload(val oldUsername: String, val newUsername: String)

			val payload = bombCall.receive<ChangeUsernamePayload>()
			val user = labyrinth.gameUserFactory.getGameUserById(uid)
				?: return@patch bombCall.respondError(toError(Localization.UserNotFound))

			if(labyrinth.gameUserFactory.getGameUserByName(payload.newUsername) == null) {
				user.username = payload.newUsername
				bombCall.respondData(user.toBO().toData())
			} else {
				bombCall.respondError(toError(Localization.UserAlreadyExists))
			}
		}

		// [/user/{id}/password]
		patch("password") {
			val uid = bombCall.parameters.getOrFail("id")

			data class ChangePasswordPayload(val oldPassword: String, val newPassword: String)

			val payload = bombCall.receive<ChangePasswordPayload>()
			val user = labyrinth.gameUserFactory.getGameUserById(uid)
				?: return@patch bombCall.respondError(toError(Localization.UserNotFound))

			if(user.validatePassword(payload.oldPassword)) {
				user.changePassword(payload.newPassword)
				bombCall.respondData(user.toBO().toData())
			} else {
				bombCall.respondError(toError(Localization.UserLoginFailed))
			}
		}

	}

}