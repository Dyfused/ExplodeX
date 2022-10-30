package explode2.booster.bomb.submods.chart

import explode2.gateau.SongChart

internal data class ChartBO(
	val id: String,
	val difficulty_class: Int,
	val difficulty_value: Int,
	val d: Double?,
	val parent_set_id: String,
)

internal fun SongChart.toBO() =
	ChartBO(id, difficultyClass, difficultyValue, d, getParentSet().id)
