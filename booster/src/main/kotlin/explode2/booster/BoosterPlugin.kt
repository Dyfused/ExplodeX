package explode2.booster

import com.github.taskeren.config.Configuration
import java.io.File

interface BoosterPlugin {
	val id: String
	val version: String

	fun onInit() {}
	fun onPreInit() {}
	fun onPostInit() {}
}

fun <T> T.subscribeEvents() {
	Booster.eventbus.register(this)
}

val BoosterPlugin.config: Configuration
	get() = Configuration(File("$id.cfg"))

fun BoosterPlugin.saveConfig() =
	config.save()

@Suppress("UNCHECKED_CAST")
val <T: BoosterPlugin> Class<T>.instance get(): T? =
	ExplodeService[this as Class<BoosterPlugin>] as? T