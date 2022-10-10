package explode2.labyrinth

import explode2.gateau.GameRecord

interface GameRecordFactory {

	fun getChartRecords(
		chartId: String,
		limit: Int = 20,
		skip: Int = 0
	): Collection<GameRecord>

	fun getPlayerBestChartRecord(
		chartId: String,
		playerId: String
	): GameRecord?

	fun createGameRecord(
		chartId: String,
		playerId: String,
		perfect: Int,
		good: Int,
		miss: Int,
		score: Int,
		r: Int?,
		id: String? = null
	): GameRecord

}