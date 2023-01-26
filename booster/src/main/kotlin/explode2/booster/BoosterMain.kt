package explode2.booster

import explode2.booster.Booster.config
import explode2.booster.Booster.dispatchEvent
import explode2.booster.event.KtorInitEvent
import explode2.booster.event.KtorModuleEvent
import explode2.booster.util.forEachExceptional
import io.github.lxgaming.classloader.ClassLoaderUtils
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import java.io.File
import java.net.BindException
import java.net.URI
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

internal val logger = LoggerFactory.getLogger("Booster")

lateinit var ktorServer: NettyApplicationEngine private set

fun main() {

	Thread.currentThread().setUncaughtExceptionHandler { _, ex ->
		logger.error("Fatal exception occurred on Main thread", ex)
	}

	// 加载插件
	logger.info("Constructing plugins")
	ExplodeService.loadBooster()

	// 检查插件数量，为零直接退出
	ExplodeService.validateStatus()

	logger.info("Pre-initializing plugins")
	ExplodeService.dispatchPreInit()

	logger.info("Initializing plugins")
	ExplodeService.dispatchInit()

	logger.info("Post-initializing plugins")
	ExplodeService.dispatchPostInit()

	// 保存所有配置文档的修改
	Booster.saveAllConfig()

	// 启动服务器线程
	thread(name = "ktor") {
		var addr: String
		var port: Int

		// 从配置中读
		addr = config.getString("addr", "general", "0.0.0.0", "后端监听的地址")
		port = config.getInt("port", "general", 10443, 0, 65535, "后端监听的端口")
		config.save() // 如果没有文件则把默认值写入

		// 系统配置覆写
		System.getProperty("ex.addr")?.let { addr = it }
		System.getProperty("ex.port")?.let { port = it.toInt() }

		// 获取事件配置
		val event: KtorInitEvent = KtorInitEvent(addr, port).dispatchEvent()
		addr = event.bindAddr
		port = event.bindPort

		logger.debug("Listening on ${addr}:${port}")

		ktorServer = embeddedServer(Netty, port = port, host = addr) {
			// 启动服务器后发布注册事件
			KtorModuleEvent(this).dispatchEvent()
		}
		try {
			ktorServer.start(true)
		} catch(e: BindException) {
			logger.error("Exception occurred when binding port", e)
			exitProcess(1)
		}
	}

	logger.info("Boosted!")
	// 主线程退出
}

object ExplodeService {

	// 加载插件阶段使用的标记
	private val preludeMarker = MarkerFactory.getMarker("Prelude")

	private val services: MutableMap<String, BoosterPlugin> = mutableMapOf()
	private val classToService: MutableMap<Class<out BoosterPlugin>, BoosterPlugin> = mutableMapOf()

	operator fun get(id: String): BoosterPlugin? = services[id]
	operator fun get(cls: Class<out BoosterPlugin>) = classToService[cls]

	private val PluginFolder = File("./plugins/").also { it.mkdirs() }

	fun loadBooster() {
		loadPluginJars()
		constructPlugins()
	}

	inline fun <reified T> load(): ServiceLoader<T> {
		return ServiceLoader.load(T::class.java)
	}

	/**
	 * 将外部 JAR 读取到 ClassLoader
	 */
	private fun loadPluginJars() {
		logger.info(preludeMarker, "Working directory: ${System.getProperty("user.dir")}")
		val urls = PluginFolder.listFiles { _, name -> name.endsWith(".jar") }?.onEach {
			logger.info(preludeMarker, "Scanned external jar: ${it.name}")
		}?.map(File::toURI)?.map(URI::toURL)
		if(urls == null) {
			logger.warn(preludeMarker, "Plugins folder is not existent or something else happened")
		} else {
			urls.forEachExceptional({ url ->
				ClassLoaderUtils.appendToClassPath(url)
			}) { url, ex ->
				logger.error(preludeMarker, "Exception occurred when loading external jar: $url", ex)
				// no-op
			}
		}
	}

	/**
	 * 将读取到的 JAR 中的 [BoosterPlugin] 实例化，并注册到表中
	 */
	private fun constructPlugins() {
		load<BoosterPlugin>().stream().forEach { provider ->
			runCatching {
				val plugin = provider.get()
				if(services.putIfAbsent(plugin.id, plugin) == null) {
					logger.info(preludeMarker, "Scanned plugin: ${plugin.id}(${plugin.version}) <${plugin.javaClass.simpleName}>")
					classToService[plugin.javaClass] = plugin
				} else {
					logger.warn(preludeMarker, "Failed to instantiate plugin ${plugin.id} because of id collision")
				}
			}.onFailure {
				logger.error(preludeMarker, "An exception occurred when loading plugin: ${provider.type().canonicalName}", it)
			}
		}
	}

	fun validateStatus() {
		if(services.isEmpty()) {
			logger.error(preludeMarker, "No plugin is found!")
			logger.error(preludeMarker, "You need to add at least one plugin to continue")
			exitProcess(0)
		}
	}

	fun dispatchPreInit() = services.values.forEachExceptional(BoosterPlugin::onPreInit) { plugin, exception ->
		logger.error(preludeMarker, "Exception occurred when pre-initializing plugin ${plugin.id}", exception)
	}

	fun dispatchInit() = services.values.forEachExceptional(BoosterPlugin::onInit) { plugin, exception ->
		logger.error(preludeMarker, "Exception occurred when initializing plugin ${plugin.id}", exception)
	}

	fun dispatchPostInit() = services.values.forEachExceptional(BoosterPlugin::onPostInit) { plugin, exception ->
		logger.error(preludeMarker, "Exception occurred when post-initializing plugin ${plugin.id}", exception)
	}

}