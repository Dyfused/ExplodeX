package explode2.booster.bomb.submods.chart

import explode2.booster.bomb.*
import explode2.booster.bomb.submods.toData
import explode2.booster.bomb.submods.toError
import explode2.booster.event.RouteConfigure
import explode2.labyrinth.LabyrinthPlugin.Companion.labyrinth
import io.ktor.server.routing.*
import io.ktor.server.util.*

// [/chart]
internal val chartModule: RouteConfigure = {

	// [/chart/{id}]
	get("{id}") {
		val cid = bombCall.parameters.getOrFail("id")
		val chart = labyrinth.songChartFactory.getSongChartById(cid)
			?: return@get bombCall.respondError(toError(Localization.ResourceNotFound))
		bombCall.respondData(chart.toBO().toData())
	}

}

// [/set]
internal val setModule: RouteConfigure = {

	// [/set/{id}]
	get("{id}") {
		val sid = bombCall.parameters.getOrFail("id")
		val set = labyrinth.songSetFactory.getSongSetById(sid)
			?: return@get bombCall.respondError(toError(Localization.ResourceNotFound))
		bombCall.respondData(set.toBO().toData())
	}

}