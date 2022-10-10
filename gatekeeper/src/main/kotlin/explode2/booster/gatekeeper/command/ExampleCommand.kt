package explode2.booster.gatekeeper.command

import cn.taskeren.brigadierx.*
import com.mojang.brigadier.CommandDispatcher
import explode2.booster.Booster.labyrinth
import explode2.booster.gatekeeper.GatekeeperSource
import explode2.booster.gatekeeper.logger
import explode2.gateau.Assessment
import explode2.gateau.SongState

fun CommandDispatcher<GatekeeperSource>.exampleModule() = register("example") {
	literal("user") {
		executesX {
			val u = labyrinth.gameUserFactory.createGameUser("Taskeren", "123456")
			logger.info("Added new user Taskeren, id ${u.id}")
			logger.debug("$u")
		}
	}

	literal("song") {
		executesX {
			val c = labyrinth.songChartFactory.createSongChart(5, 15)
			logger.info("Created chart ${c.id}")
			logger.debug("$c")
			val s = labyrinth.songSetFactory.createSongSet(
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
				val c = labyrinth.songChartFactory.createSongChart(5, 15)
				logger.info("Created chart ${c.id}")
				logger.debug("$c")
				val s = labyrinth.songSetFactory.createSongSet(
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

			labyrinth.assessmentInfoFactory.getAssessmentGroups()[0].setAssessmentForMedal(
				1,
				100.0,
				175.0,
				150.0,
				0.0,
				sets.map { it.id })

			val ass = labyrinth.assessmentInfoFactory.getAssessmentGroups()[0].getAssessmentForMedal(1)!!
			logger.debug("$ass")
			logger.info("Created Assessment for level ${ass.id}, with ${ass.assessmentChartIds.size} charts inside")
		}
	}
}