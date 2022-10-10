package explode2.booster

import explode2.booster.Booster.dispatchEvent
import explode2.booster.event.KtorInitEvent
import explode2.booster.event.KtorModuleEvent
import explode2.booster.util.forEachExceptional
import explode2.booster.util.mapExceptional
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.thread
import kotlin.streams.asSequence
import kotlin.system.exitProcess

internal val logger = LoggerFactory.getLogger("Booster")

private val serviceLoader =
	ServiceLoader.load(BoosterPlugin::class.java)

lateinit var ktorServer: NettyApplicationEngine private set

fun main() {

	ServiceManager.loadServiceLoader(serviceLoader)

	ServiceManager.validateStatus()

	logger.info("Pre-initializing plugins")
	ServiceManager.dispatchPreInit()

	logger.info("Initializing plugins")
	ServiceManager.dispatchInit()

	logger.info("Post-initializing plugins")
	ServiceManager.dispatchPostInit()

	val initEvent: KtorInitEvent = KtorInitEvent().dispatchEvent()

	fun boostKtor() {
		ktorServer = embeddedServer(Netty, port = initEvent.bindPort, host = initEvent.bindAddr) {
			// 启动服务器后发布注册事件
			KtorModuleEvent(this).dispatchEvent()
		}
		ktorServer.start(true)
	}

	if(initEvent.newThread) {
		thread(name = "Ktor", block = ::boostKtor)
	} else {
		boostKtor()
	}

	logger.info("Boosted!")
}

object ServiceManager {

	private val services: MutableMap<String, BoosterPlugin> = mutableMapOf()

	operator fun get(id: String): BoosterPlugin? = services[id]

	fun loadServiceLoader(serviceLoader: ServiceLoader<BoosterPlugin>) {
		serviceLoader.stream().forEach { provider ->
			runCatching {
				loadSinglePlugin(provider)
			}.onFailure {
				logger.error("An exception occurred when loading plugin: ${provider.type().canonicalName}", it)
			}
		}
	}

	private fun loadSinglePlugin(provider: ServiceLoader.Provider<BoosterPlugin>) {
		val plugin = provider.get()
		if(services.putIfAbsent(plugin.id, plugin) == null) {
			logger.info("Scanned plugin: ${plugin.id}(${plugin.version})")
		} else {
			logger.warn("Failed to instantiate plugin ${plugin.id} because of id collision")
		}
	}

	fun validateStatus() {
		if(services.isEmpty()) {
			logger.error("No plugin is found!")
			logger.error("You need to add at least one plugin to continue")
			exitProcess(0)
		}
	}

	fun dispatchPreInit() = services.values.forEachExceptional(BoosterPlugin::onPreInit) { plugin, exception ->
		logger.error("Exception occurred when pre-initializing plugin ${plugin.id}", exception)
	}

	fun dispatchInit() = services.values.forEachExceptional(BoosterPlugin::onInit) { plugin, exception ->
		logger.error("Exception occurred when initializing plugin ${plugin.id}", exception)
	}

	fun dispatchPostInit() = services.values.forEachExceptional(BoosterPlugin::onPostInit) { plugin, exception ->
		logger.error("Exception occurred when post-initializing plugin ${plugin.id}", exception)
	}

}