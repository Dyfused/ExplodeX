package explode2.booster.bomb.submods.chart

import explode2.gateau.SongChart

internal data class ChartBO(
	val id: String,
	val difficulty_class: Int,
	val difficulty_value: Int,
	val d: Double?
)

internal fun SongChart.toBO() =
	ChartBO(id, difficultyClass, difficultyValue, d)
