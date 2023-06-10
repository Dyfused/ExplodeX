package explode2.booster.bomb.submods.user

import explode2.booster.Explode
import explode2.booster.bomb.*
import explode2.booster.bomb.submods.BombPrincipal
import explode2.booster.bomb.submods.chart.toBO
import explode2.booster.bomb.submods.toData
import explode2.booster.bomb.submods.toError
import explode2.booster.event.RouteConfigure
import explode2.booster.resource.ResourceReceiver
import explode2.gateau.ScoreOrRanking
import explode2.labyrinth.GameUserRepository
import explode2.labyrinth.SongChartRepository
import explode2.labyrinth.SongSetRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.utils.io.core.*
import org.koin.ktor.ext.inject

// [/user]
internal val userModule: RouteConfigure = {

	val userRepo by inject<GameUserRepository>()
	val songRepo by inject<SongSetRepository>()
	val chartRepo by inject<SongChartRepository>()

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
		val user = userRepo.getGameUserById(uid)
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

		if(userRepo.getGameUserByName(username) != null) {
			bombCall.respondError(toError(Localization.UserAlreadyExists))
		} else {
			val user = userRepo.createGameUser(username, password)
			bombCall.respondData(user.toBO().toData())
		}
	}

	// [/user/search]
	post("search") {
		data class SearchUsernamePayload(val username: String)

		val payload = bombCall.receive<SearchUsernamePayload>()

		val user = userRepo.getGameUserByName(payload.username)
		if(user != null) {
			bombCall.respondData(user.toBO().toData())
		} else {
			bombCall.respondError(toError(Localization.UserNotFound))
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

			val user = userRepo.getGameUserById(uid)
				?: return@get bombCall.respondError(toError(Localization.UserNotFound))

			// 成绩列表
			val list = user.calculateBestRecords(limit, if(scoreMode) ScoreOrRanking.Score else ScoreOrRanking.Ranking)
				.map {
					it.toBO(
						(songRepo.getSongSetByChart(it.playedChartId) ?: error("invalid record: cannot get the set (contains chart: ${it.playedChartId})")).toBO(),
						(chartRepo.getSongChartById(it.playedChartId) ?: error("invalid record: cannot get the chart (${it.playedChartId})")).toBO()
					)
				}

			bombCall.respondData(list.toData())
		}

		// [/user/{id}/last]
		get("last") {
			val uid = bombCall.parameters.getOrFail("id")

			// 请求长度，默认为 20
			val limit = bombCall.parameters["limit"]?.toIntOrNull()?.clamp(1..100) ?: 20

			val user = userRepo.getGameUserById(uid)
				?: return@get bombCall.respondError(toError(Localization.UserNotFound))

			// 成绩列表
			val list = user.calculateLastRecords(limit).map {
				it.toBO(
					(songRepo.getSongSetByChart(it.playedChartId) ?: error("invalid record: cannot get the set (contains chart: ${it.playedChartId})")).toBO(),
					(chartRepo.getSongChartById(it.playedChartId) ?: error("invalid record: cannot get the chart (${it.playedChartId})")).toBO()
				)
			}

			bombCall.respondData(list.toData())
		}

		// [/user/{id}/username]
		patch("username") {
			val uid = bombCall.parameters.getOrFail("id")

			data class ChangeUsernamePayload(val password: String, val newUsername: String)

			val payload = bombCall.receive<ChangeUsernamePayload>()
			val user = userRepo.getGameUserById(uid)
				?: return@patch bombCall.respondError(toError(Localization.UserNotFound))

			// 检查密码
			if(!user.validatePassword(payload.password)) return@patch bombCall.respondError(toError(Localization.UserLoginFailed))

			// 检查重名
			if(userRepo.getGameUserByName(payload.newUsername) == null) {
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
			val user = userRepo.getGameUserById(uid)
				?: return@patch bombCall.respondError(toError(Localization.UserNotFound))

			if(user.validatePassword(payload.oldPassword)) {
				user.changePassword(payload.newPassword)
				bombCall.respondData(user.toBO().toData())
			} else {
				bombCall.respondError(toError(Localization.UserLoginFailed))
			}
		}

		// [/user/{id}/avatar]
		patch("avatar") {
			val uid = bombCall.parameters.getOrFail("id")

			data class ChangeAvatarPayload(val error: Boolean, val password: String, val avatarByte: ByteArray)

			val payload = run {
				var password: String? = null
				var avatarByte: ByteArray? = null
				var responded = false

				bombCall.receiveMultipart().forEachPart {
					if(it.name == "password") {
						if(it is PartData.FormItem) {
							password = it.value
						} else {
							bombCall.respondError(
								toError(Localization.UserLoginFailed),
								status = HttpStatusCode.Unauthorized
							)
							responded = true
							return@forEachPart
						}
					}

					if(it.name == "avatar") {
						when(it) {
							is PartData.BinaryItem -> {
								avatarByte = it.provider().readBytes()
							}

							is PartData.FileItem -> {
								avatarByte = it.provider().readBytes()
							}

							else -> {
								bombCall.respondError(
									toError(Localization.InvalidResourceType),
									status = HttpStatusCode.BadRequest
								)
								responded = true
								return@forEachPart
							}
						}
					}
				}
				if(password != null && avatarByte != null) {
					return@run ChangeAvatarPayload(responded, password!!, avatarByte!!)
				} else {
					return@run null
				}
			} ?: return@patch bombCall.respondError(
				toError(Localization.InvalidArgument),
				status = HttpStatusCode.BadRequest
			)

			val user = userRepo.getGameUserById(uid)
				?: return@patch bombCall.respondError(
					toError(Localization.UserNotFound),
					status = HttpStatusCode.Unauthorized
				)

			if(user.validatePassword(payload.password)) {
				val service = Explode.resource
				if(service is ResourceReceiver) {
					service.uploadUserAvatar(user.id, user.id, payload.avatarByte)
					bombCall.respond(service.getUserAvatar(user.id, user.id))
				} else {
					bombCall.respondError(
						toError(Localization.UnsupportedOperation),
						status = HttpStatusCode.MethodNotAllowed
					)
				}
			} else {
				bombCall.respondError(toError(Localization.UserLoginFailed), status = HttpStatusCode.Unauthorized)
			}
		}


	}

}