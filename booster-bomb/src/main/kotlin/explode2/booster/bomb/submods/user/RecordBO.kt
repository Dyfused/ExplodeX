package explode2.booster.bomb.submods.user

import explode2.booster.bomb.submods.chart.*
import explode2.booster.bomb.submods.chart.SetBO
import explode2.booster.bomb.submods.chart.toBO
import explode2.gateau.GameRecord
import explode2.labyrinth.LabyrinthPlugin.Companion.labyrinth
import java.time.format.DateTimeFormatter

internal data class RecordBO(
	val id: String,
	val player_id: String,
	val set_id: String,
	val chart_id: String,
	val perfect: Int,
	val good: Int,
	val miss: Int,
	val score: Int,
	val upload_time: String,
	val r: Int?,
	val ranking: Int?,
	val set_info: SetBO,
	val chart_info: ChartBO
)

internal fun GameRecord.toBO(): RecordBO {
	val c = labyrinth.songChartFactory.getSongChartById(playedChartId) ?: error("unexpected record with missing chart $playedChartId")
	val s = labyrinth.songSetFactory.getSongSetByChart(playedChartId) ?: error("unexpected chart with missing set $playedChartId")
	return RecordBO(
		id,
		playerId, s.id, playedChartId,
		perfect, good, miss, score,
		uploadTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
		r, ranking,
		s.toBO(),
		c.toBO(),
	)
}