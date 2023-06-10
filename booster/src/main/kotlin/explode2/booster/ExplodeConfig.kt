package explode2.booster

import com.github.taskeren.config.Configuration
import explode2.booster.util.forEachExceptional
import java.io.File

object ExplodeConfig {

	private val configs: MutableSet<Configuration> = mutableSetOf()

	/**
	 * 创建一个配置文档
	 *
	 * 使用此方法创建的配置文档在所有插件加载完成后会自动保存一次
	 */
	fun get(subject: String): Configuration =
		Configuration(File("$subject.cfg")).apply(configs::add)

	/**
	 * 保存所有配置文档，当所有插件加载完成后执行一次
	 */
	fun saveAllConfig() = configs.forEachExceptional(Configuration::save) { config, ex ->
		logger.warn("Exception occurred when saving config($config)", ex)
	}

}