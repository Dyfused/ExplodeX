package explode2.booster

import com.github.taskeren.config.Configuration

interface BoosterPlugin {
	val id: String
	val version: String

	fun onInit() {}
	fun onPreInit() {}
	fun onPostInit() {}

	companion object {
		fun getPlugin(pluginClass: Class<out BoosterPlugin>): BoosterPlugin? {
			return ExplodeService[pluginClass]
		}

		inline fun <reified T: BoosterPlugin> getPlugin(): BoosterPlugin? {
			return ExplodeService[T::class.java]
		}
	}
}

fun <T> T.subscribeEvents() {
	Booster.eventbus.register(this)
}

val BoosterPlugin.config: Configuration
	get() = Booster.config(id)

fun BoosterPlugin.saveConfig() =
	config.save()

@Suppress("UNCHECKED_CAST")
val <T: BoosterPlugin> Class<T>.instance get(): T? =
	ExplodeService[this as Class<BoosterPlugin>] as? T