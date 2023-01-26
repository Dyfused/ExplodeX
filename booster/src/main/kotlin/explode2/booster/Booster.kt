package explode2.booster

import com.github.taskeren.config.Configuration
import explode2.booster.util.forEachExceptional
import io.ktor.server.engine.*
import org.greenrobot.eventbus.EventBus
import org.slf4j.MarkerFactory
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.logging.Level

object Booster {

	val config = Configuration(File("explode.cfg"))

	val eventbus: EventBus = EventBus.builder()
		.logger(object : org.greenrobot.eventbus.Logger {
			private val marker = MarkerFactory.getMarker("EventBus")

			override fun log(level: Level, msg: String) = when(level) {
				Level.FINER, Level.FINEST -> logger.trace(marker, msg)
				Level.FINE, Level.INFO -> logger.info(marker, msg)
				Level.WARNING -> logger.warn(marker, msg)
				Level.SEVERE -> logger.error(marker, msg)
				else -> {}
			}

			override fun log(level: Level, msg: String, th: Throwable) = when(level) {
				Level.FINER, Level.FINEST -> logger.trace(marker, msg, th)
				Level.FINE, Level.INFO -> logger.info(marker, msg, th)
				Level.WARNING -> logger.warn(marker, msg, th)
				Level.SEVERE -> logger.error(marker, msg, th)
				else -> {}
			}
		})
		.sendNoSubscriberEvent(false)
		.logNoSubscriberMessages(false)
		.logSubscriberExceptions(false)
		.build()

	/**
	 * 用于插件间传递数据的引用表
	 */
	@Suppress("unused")
	val globalRef: MutableMap<String, Any?> = mutableMapOf()

	fun <T> T.dispatchEvent(): T = apply { eventbus.post(this) }

	@Suppress("unused")
	fun stopKtor(graceSec: Long = 5, timeOutSec: Long = graceSec) =
		ktorServer.stop(graceSec, timeOutSec, TimeUnit.SECONDS)

	init {
		eventbus.register(BoosterGeneralEventHandler)
	}

	private val configs: MutableSet<Configuration> = mutableSetOf(
		config
	)

	/**
	 * 创建一个配置文档
	 *
	 * 使用此方法创建的配置文档在所有插件加载完成后会自动保存一次
	 */
	fun config(subject: String): Configuration =
		Configuration(File("$subject.cfg")).apply(configs::add)

	/**
	 * 保存所有配置文档，当所有插件加载完成后执行一次
	 */
	fun saveAllConfig() = configs.forEachExceptional(Configuration::save) { config, ex ->
		logger.warn("Exception occurred when saving config($config)", ex)
	}

}