package explode2.booster.gatekeeper

import cn.taskeren.brigadierx.*
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import explode2.booster.BoosterPlugin
import explode2.booster.gatekeeper.command.exampleModule
import explode2.labyrinth.LabyrinthPlugin
import net.minecrell.terminalconsole.SimpleTerminalConsole
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread
import kotlin.system.exitProcess

val logger: Logger = LoggerFactory.getLogger("Gatekeeper")

class GatekeeperPlugin : BoosterPlugin {

	override val id: String = "gatekeeper"
	override val version: String = "unspecified"

	override fun onInit() {
		GatekeeperConsole.init()
	}

	override fun onPostInit() {
		thread(name = "Gatekeeper", isDaemon = true) {
			GatekeeperConsole.start()
			logger.debug("Gatekeeper console terminated")
		}
	}

	object GatekeeperConsole : SimpleTerminalConsole(), GatekeeperSource {

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
						logger.info("Labyrinth: ${LabyrinthPlugin.labyrinth.javaClass.canonicalName}")
					}
				}

				exampleModule()
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