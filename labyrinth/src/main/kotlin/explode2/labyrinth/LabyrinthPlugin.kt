package explode2.labyrinth

import explode2.booster.BoosterPlugin
import explode2.booster.instance
import java.util.*

class LabyrinthPlugin : BoosterPlugin {

	override val id: String = "labyrinth"
	override val version: String = "1.0.0"

	fun getProvider(): LabyrinthProvider {
		return providerCache
	}

	private val providerCache: LabyrinthProvider by lazy {
		ServiceLoader.load(LabyrinthProvider::class.java).findFirst().get()
	}

	companion object {
		/**
		 * 获取 [LabyrinthProvider] 实例
		 */
		val labyrinth get() = LabyrinthPlugin::class.java.instance?.getProvider() ?: error("Labyrinth is not installed yet")
	}
}