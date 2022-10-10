package explode2.booster

import com.electronwill.nightconfig.core.file.FileConfig
import explode2.labyrinth.LabyrinthProvider
import io.ktor.server.engine.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Logger.SystemOutLogger
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import kotlin.concurrent.thread
import kotlin.io.path.Path
import kotlin.system.exitProcess

object Booster {

	val config = FileConfig.builder(Path("explode.config.toml"))
		.autoreload().autosave().charset(Charsets.UTF_8).build()

	val labyrinth = LabyrinthProvider.getProvider()

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
				Level.FINER, Level.FINEST -> logger.trace(msg, th)
				Level.FINE, Level.INFO -> logger.info(msg, th)
				Level.WARNING -> logger.warn(msg, th)
				Level.SEVERE -> logger.error(msg, th)
				else -> {}
			}
		})
		.build()

	/**
	 * 用于插件间传递数据的引用表
	 */
	val globalRef: MutableMap<String, Any?> = mutableMapOf()

	fun <T> T.dispatchEvent(): T = apply { eventbus.post(this) }

	fun stopKtor(graceSec: Long = 5, timeOutSec: Long = graceSec) =
		ktorServer.stop(graceSec, timeOutSec, TimeUnit.SECONDS)

}