package explode2.booster.bomb.submods.user

import explode2.booster.bomb.submods.chart.ChartBO
import explode2.booster.bomb.submods.chart.SetBO
import explode2.gateau.GameRecord
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

internal fun GameRecord.toBO(s: SetBO, c: ChartBO) = RecordBO(
	id,
	playerId, s.id, playedChartId,
	perfect, good, miss, score,
	uploadTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
	r, ranking,
	s,
	c,
)