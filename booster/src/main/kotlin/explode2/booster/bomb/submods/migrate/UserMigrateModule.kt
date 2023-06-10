package explode2.booster.bomb.submods.migrate

import explode2.booster.bomb.Localization
import explode2.booster.bomb.bombCall
import explode2.booster.bomb.respondData
import explode2.booster.bomb.respondError
import explode2.booster.bomb.submods.BombPrincipal
import explode2.booster.bomb.submods.chart.toBO
import explode2.booster.bomb.submods.toData
import explode2.booster.bomb.submods.toError
import explode2.booster.bomb.submods.user.toBO
import explode2.booster.event.RouteConfigure
import explode2.labyrinth.SongChartRepository
import explode2.labyrinth.SongSetRepository
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

// [/migrate]
internal val migrationModule: RouteConfigure = {

	val songRepo by inject<SongSetRepository>()
	val chartRepo by inject<SongChartRepository>()

	authenticate {
		post {
			val user = bombCall.principal<BombPrincipal>()?.user
				?: return@post bombCall.respondError(toError(Localization.NotAuthenticatedOrUserNotFound))

			val skip = bombCall.parameters["skip"]?.toIntOrNull() ?: 0
			val limit = bombCall.parameters["limit"]?.toIntOrNull() ?: 100

			val recs = user.getAllRecords(limit, skip)

			bombCall.respondData(UserMigrationBO(user.toBO(), recs.map {
				it.toBO(
					(songRepo.getSongSetByChart(it.playedChartId) ?: error("invalid record: cannot get the set (contains chart: ${it.playedChartId})")).toBO(),
					(chartRepo.getSongChartById(it.playedChartId) ?: error("invalid record: cannot get the chart (${it.playedChartId})")).toBO()
				)
			}).toData())
		}
	}

}