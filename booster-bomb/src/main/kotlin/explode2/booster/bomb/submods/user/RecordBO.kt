package explode2.booster.bomb.submods.user

import explode2.gateau.GameRecord
import java.time.format.DateTimeFormatter

data class RecordBO(
	val id: String,
	val player_id: String,
	val chart_id: String,
	val perfect: Int,
	val good: Int,
	val miss: Int,
	val score: Int,
	val upload_time: String,
	val r: Int?,
	val ranking: Int?,
)

fun GameRecord.toBO() =
	RecordBO(
		id,
		playerId, playedChartId,
		perfect, good, miss, score,
		uploadTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
		r, ranking
	)