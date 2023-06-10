package explode2.booster.gatekeeper

import cn.taskeren.brigadierx.executesX
import cn.taskeren.brigadierx.register
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import explode2.booster.BoosterPlugin
import explode2.booster.Explode
import explode2.booster.gatekeeper.command.addGenerateExampleModule
import explode2.booster.gatekeeper.command.addUserCommand
import explode2.labyrinth.AssessmentInfoRepository
import explode2.labyrinth.GameUserRepository
import explode2.labyrinth.SongChartRepository
import explode2.labyrinth.SongSetRepository
import net.minecrell.terminalconsole.SimpleTerminalConsole
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread
import kotlin.system.exitProcess

val logger: Logger = LoggerFactory.getLogger("Gatekeeper")

class GatekeeperPlugin : BoosterPlugin {

	override val id: String = "gatekeeper"
	override val version: String = "unspecified"

	private val console: GatekeeperConsole = GatekeeperConsole()

	internal val userRepo by inject<GameUserRepository>()
	internal val assInfoRepo by inject<AssessmentInfoRepository>()
	internal val songRepo by inject<SongSetRepository>()
	internal val chartRepo by inject<SongChartRepository>()

	override fun onInit() {
		console.init()
	}

	override fun onPostInit() {
		thread(name = "Gatekeeper", isDaemon = true) {
			console.start()
			logger.debug("Gatekeeper console terminated")
		}
	}

	inner class GatekeeperConsole : SimpleTerminalConsole(), GatekeeperSource, KoinComponent {

		private val dispatcher = CommandDispatcher<GatekeeperSource>()

		fun init() {
			dispatcher.apply {
				register("exit") {
					executesX {
						logger.info("Executing termination sequence")
						exitProcess(0)
					}
				}

				register("labyrinth") {
					executesX {
						logger.info("Labyrinth: ${Explode.labyrinth.javaClass.canonicalName}")
					}
				}

				addGenerateExampleModule(this@GatekeeperPlugin)
				addUserCommand(this@GatekeeperPlugin)
			}
		}

		override fun isRunning(): Boolean {
			return true
		}

		override fun runCommand(command: String) {
			logger.debug("Executing $command")
			try {
				dispatcher.execute(command, this)
			} catch(e: CommandSyntaxException) {
				logger.info(e.message)
			} catch(e: Throwable) {
				logger.error("Exception occurred and not caught on executing command", e)
			}
		}

		override fun shutdown() {
			logger.info("Terminated by Gatekeeper")
			exitProcess(0)
		}
	}
}