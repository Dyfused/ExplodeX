package explode2.gateau

import java.time.OffsetDateTime

interface GameUser {

	val id: String

	var username: String

	var coin: Int
	var diamond: Int

	var ppTime: OffsetDateTime

	var isReviewer: Boolean

	val SongSet.isOwned: Boolean

	val ownedSets: List<SongSet>

	fun calculateR(): Int

	fun calculateHighestGoldenMedal(): Int
	fun changePassword(password: String)

	fun validatePassword(password: String): Boolean

	fun giveSet(setId: String)

	fun calculateLastRecords(limit: Int): List<GameRecord>
	fun calculateBestRecords(limit: Int, sortedBy: ScoreOrRanking): List<GameRecord>
}