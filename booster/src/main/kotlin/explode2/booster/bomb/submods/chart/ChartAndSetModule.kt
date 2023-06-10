package explode2.booster.bomb.submods.chart

import explode2.booster.bomb.Localization
import explode2.booster.bomb.bombCall
import explode2.booster.bomb.respondData
import explode2.booster.bomb.respondError
import explode2.booster.bomb.submods.toData
import explode2.booster.bomb.submods.toError
import explode2.booster.event.RouteConfigure
import explode2.labyrinth.SongChartRepository
import explode2.labyrinth.SongSetRepository
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

// [/chart]
internal val chartModule: RouteConfigure = {

	val chartRepo by inject<SongChartRepository>()

	// [/chart/{id}]
	get("{id}") {
		val cid = bombCall.parameters.getOrFail("id")
		val chart = chartRepo.getSongChartById(cid)
			?: return@get bombCall.respondError(toError(Localization.ResourceNotFound))
		bombCall.respondData(chart.toBO().toData())
	}

}

// [/set]
internal val setModule: RouteConfigure = {

	val songRepo by inject<SongSetRepository>()

	// [/set/{id}]
	get("{id}") {
		val sid = bombCall.parameters.getOrFail("id")
		val set = songRepo.getSongSetById(sid)
			?: return@get bombCall.respondError(toError(Localization.ResourceNotFound))
		bombCall.respondData(set.toBO().toData())
	}

}