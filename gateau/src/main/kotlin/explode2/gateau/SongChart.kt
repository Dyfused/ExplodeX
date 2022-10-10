package explode2.gateau

interface SongChart {

	val id: String

	var difficultyClass: Int
	var difficultyValue: Int

	var d: Double?

	fun getRankingRecords(limit: Int = 20, skip: Int = 0): Collection<GameRecord>

	fun getRankingPlayerRecord(userId: String): GameRecord?

}