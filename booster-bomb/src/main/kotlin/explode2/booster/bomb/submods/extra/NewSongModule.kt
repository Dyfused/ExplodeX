package explode2.booster.bomb.submods.extra

import explode2.booster.bomb.*
import explode2.booster.bomb.submods.BombPrincipal
import explode2.booster.bomb.submods.ExceptionContext
import explode2.booster.bomb.submods.chart.toBO
import explode2.booster.bomb.submods.toData
import explode2.booster.bomb.submods.toError
import explode2.booster.event.RouteConfigure
import explode2.gateau.GameUser
import explode2.gateau.Permission
import explode2.gateau.SongChart
import explode2.gateau.SongState
import explode2.labyrinth.LabyrinthPlugin.Companion.labyrinth
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import java.time.OffsetDateTime

// [/upload]
internal val newSongModule: RouteConfigure = {

	authenticate {
		data class NewChartPayload(
			val difficulty_class: Int,
			val difficulty_value: Int,
			val d: Double? = null,
			val id: String? = null,
		)

		data class NewSongPayload(
			val music_name: String,
			val music_composer: String,
			val introduction: String,
			val noter_name: String,
			val charts: List<NewChartPayload>,
			val category: Int,
			val hidden: Boolean = false,
			val reviewing: Boolean = true,
			val id: String? = null,
			val coin_price: Int = 0,
			val publish_time: OffsetDateTime = OffsetDateTime.now(),
			val noter_user: GameUser? = null
		)

		val permissionUpload = Permission.getOrCreate("bomb.upload")

		post<NewSongPayload> {
			val (
				musicName,
				musicComposer,
				introduction,
				noterName,
				charts,
				category,
				hidden,
				reviewing,
				id,
				coinPrice,
				publishTime,
				noterUser
			) = it

			val auth = bombCall.principal<BombPrincipal>() ?: return@post bombCall.respondError(toError(Localization.UserLoginFailed))
			val user = auth.user ?: return@post bombCall.respondError(toError(Localization.UserNotFound))

			if(!user.hasPermission(permissionUpload)) {
				return@post bombCall.respondError(toError(Localization.PermissionDenied))
			}

			logger.info("Received new song uploading request from ${user.username}(${user.id})")

			// 记录异常，在返回时方便管理员排查处理
			val exceptions = mutableListOf<ExceptionContext>()

			// 注册谱面
			val registeredCharts =
				charts.mapNotNull { (clazz, value, d, id) ->
					try {
						labyrinth.songChartFactory.createSongChart(clazz, value, id, d)
					} catch(e: Throwable) {
						exceptions += ExceptionContext("Failed to register Chart(id=$id, class=$clazz, value=$value)", e)
						null
					}
				}

			// 注册曲目
			val registeredSet = try {
				labyrinth.songSetFactory.createSongSet(
					musicName,
					musicComposer,
					introduction,
					noterName,
					registeredCharts.map(SongChart::id),
					SongState.of(category, hidden, reviewing),
					id,
					coinPrice,
					publishTime,
					noterUser ?: user // 默认用户为 null 时使用登录的用户
				)
			} catch(e: Throwable) {
				exceptions += ExceptionContext("Failed to register Song", e)
				null
			}

			// 成功就返回曲目，否则就返回错误信息
			if(registeredSet != null && exceptions.isEmpty()) {
				bombCall.respondData(registeredSet.toBO().toData())
			} else {
				bombCall.respondError(toError("registering", contexts = exceptions))
			}
		}
	}

}