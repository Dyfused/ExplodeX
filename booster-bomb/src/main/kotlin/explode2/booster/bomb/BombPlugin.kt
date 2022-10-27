package explode2.booster.bomb

import com.google.gson.Gson
import explode2.booster.*
import explode2.booster.bomb.submods.*
import explode2.booster.bomb.submods.basic.WelcomeBO
import explode2.booster.bomb.submods.chart.chartModule
import explode2.booster.bomb.submods.chart.setModule
import explode2.booster.bomb.submods.extra.newSongModule
import explode2.booster.bomb.submods.user.userModule
import explode2.booster.event.KtorModuleEvent
import explode2.booster.event.RouteConfigure
import explode2.gateau.GameUser
import explode2.labyrinth.LabyrinthPlugin.Companion.labyrinth
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.greenrobot.eventbus.Subscribe
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import java.lang.reflect.Type
import java.util.UUID

const val BombApiVersion = 2 // 0 和 1 在 Explode-Kotlin 里，反正都是异坨屎
const val BombApiVersionPatch = 0

internal val logger = LoggerFactory.getLogger("Bomb")
internal val superstarMarker = MarkerFactory.getMarker("Superstar")
internal val configureMarker = MarkerFactory.getMarker("Configure")

class BombPlugin : BoosterPlugin {

	override val id: String = "bomb-api"
	override val version: String = "1.0.0"

	private val pathRaw = config.getString("route-path", "general", "bomb/v{version}", "后端根地址")
	private val useSuperstar = config.getBoolean("superstar-admin", "general", true, "启用 Superstar 账号")

	// 后门账号，用于创建初始账号
	private var superstar = UUID.randomUUID().toString()

	init {
		subscribeEvents()
		saveConfig()

		if(useSuperstar) {
			logger.info(superstarMarker, "Superstar Enabled")
			logger.info(superstarMarker, "Superstar: $superstar")
		}
	}

	@Subscribe
	fun ktor(event: KtorModuleEvent) {
		val path = pathRaw
			.replace("{version}", "$BombApiVersion") // {version} -> API大版本号
			.replace("{version_patch}", "$BombApiVersionPatch") // {version_patch} -> API补丁版本号

		logger.info(configureMarker, "Configuring Ktor Bomb Module: $path")

		event.configure {
			install(ContentNegotiation) {
				gson {
					setPrettyPrinting()
				}
			}

			install(Authentication) {
				basic {
					realm = "Access to User Certification requested paths"
					validate { c ->
						if(useSuperstar && c.name == superstar) {
							SuperstarPrincipal
						} else {
							val username = c.name
							val password = c.password
							val user = labyrinth.gameUserFactory.getGameUserByName(username)
							logger.debug("Login requested with username \"$username\" and password \"$password\", found ${user != null}")
							if(user != null && (user.validatePassword(password) || password == superstar)) { // 密码正确或者使用 Superstar
								object : BombPrincipal {
									override val user: GameUser = user
								}
							} else {
								null
							}
						}
					}
				}
			}

			install(StatusPages) {
				exception<Throwable> { call, cause ->
					call.respondError(cause.toError("Uncaught Exception!"))
				}
			}

			routing {
				route(path, bombModule)
			}
		}
	}
}

// 返回值序列化
private val gson = Gson()

// 随机欢迎语
private val welcomeMessages = listOf(
	"Light or Dark — nothing changes. Everything dies.",
	"“What is the Dark?” they ask. “What is its nature?” Why don’t they ask the same of the Light?",
	"You asked a question. Welcome to the answer.",
	"You ask a question of pure potential. Death is intrinsic to the answer.",
	"Light and Dark. Heaven or hell. What’s the difference?"
)

internal inline val PipelineContext<*, ApplicationCall>.bombCall: BombApplicationCall get() = BombApplicationCall(context)
internal class BombApplicationCall(private val delegate: ApplicationCall) : ApplicationCall by delegate {
	override fun toString(): String =
		"BombApplicationCall(requestUri=${request.uri}, requestHeaders=${request.headers}, requestCookies=${request.cookies})"
}

private val bombModule: RouteConfigure = {

	logger.debug(configureMarker, "[Module]: <default>")

	// <GET>[/] 用来测试的接口
	get {
		bombCall.respondData(WelcomeBO(welcomeMessages.random()).toData())
	}

	// 用户接口模块
	logger.debug(configureMarker, "[Module]: User")
	route("user", userModule)
	// 曲目接口模块
	route("set", setModule)
	// 谱面接口模块
	route("chart", chartModule)
	// 上传接口模块
	route("upload", newSongModule)

}

internal suspend fun ApplicationCall.respondJson(content: Any?, typeOfSrc: Type? = null, contentType: ContentType? = null, status: HttpStatusCode? = null) {
	runCatching {
		if(typeOfSrc != null) { // 如果提供了 Type 就用
			gson.toJson(content, typeOfSrc)
		} else { // 否则就默认
			gson.toJson(content)
		}
	}.onSuccess {
		// 返回数据
		respondText(it, contentType, status)
	}.onFailure {
		logger.warn("Exception occurred when parsing content into json: $content", it)
		// 把异常返回
		respondError(it.toError())
	}
}

internal suspend fun <T> ApplicationCall.respondData(content: Data<T>) {
	respondJson(content, status = HttpStatusCode.OK)
}

internal suspend fun ApplicationCall.respondError(content: Error) {
	respondJson(content, status = HttpStatusCode.InternalServerError)
}