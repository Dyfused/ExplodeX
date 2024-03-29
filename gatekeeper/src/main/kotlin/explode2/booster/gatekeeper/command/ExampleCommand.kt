package explode2.booster.gatekeeper.command

import cn.taskeren.brigadierx.executesX
import cn.taskeren.brigadierx.literal
import cn.taskeren.brigadierx.register
import com.mojang.brigadier.CommandDispatcher
import explode2.booster.gatekeeper.GatekeeperPlugin
import explode2.booster.gatekeeper.GatekeeperSource
import explode2.booster.gatekeeper.logger
import explode2.gateau.SongState

fun CommandDispatcher<GatekeeperSource>.addGenerateExampleModule(plugin: GatekeeperPlugin) = register("example") {

	val userRepo = plugin.userRepo
	val songRepo = plugin.songRepo
	val chartRepo = plugin.chartRepo
	val assInfoRepo = plugin.assInfoRepo

	literal("user") {
		executesX {
			val u = userRepo.createGameUser("Taskeren", "123456")
			logger.info("Added new user Taskeren, id ${u.id}")
			logger.debug("$u")
		}
	}

	literal("song") {
		executesX {
			val c = chartRepo.createSongChart(5, 15)
			logger.info("Created chart ${c.id}")
			logger.debug("$c")
			val s = songRepo.createSongSet(
				"TestMusic",
				"Me",
				"",
				"NoterName",
				listOf(c.id),
				SongState.ofUnRanked()
			)
			logger.info("Created set ${s.musicName}, id ${s.id}")
			logger.debug("$s")
		}
	}

	literal("assessment") {
		executesX {
			val sets = List(4) {
				val c = chartRepo.createSongChart(5, 15)
				logger.info("Created chart ${c.id}")
				logger.debug("$c")
				val s = songRepo.createSongSet(
					"TestMusic",
					"Me",
					"",
					"NoterName",
					listOf(c.id),
					SongState.ofUnRanked()
				)
				logger.info("Created set ${s.musicName}, id ${s.id}")
				logger.debug("$s")
				c
			}

			assInfoRepo.getAssessmentGroups()[0].setAssessmentForMedal(
				1,
				100.0,
				175.0,
				150.0,
				0.0,
				sets.map { it.id })

			val ass = assInfoRepo.getAssessmentGroups()[0].getAssessmentForMedal(1)!!
			logger.debug("$ass")
			logger.info("Created Assessment for level ${ass.id}, with ${ass.assessmentChartIds.size} charts inside")
		}
	}
}