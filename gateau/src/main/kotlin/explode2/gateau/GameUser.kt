package explode2.gateau

import java.time.OffsetDateTime

interface GameUser {

	val id: String

	var username: String

	var coin: Int
	var diamond: Int

	var ppTime: OffsetDateTime

	@Deprecated("Use Permission System instead")
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

	fun getAllRecords(limit: Int, skip: Int): List<GameRecord>

	val omegaCount: Int

	fun hasPermission(permissionKey: String): Boolean
	fun hasPermission(permission: Permission): Boolean

	fun grantPermission(permissionKey: String)
	fun revokePermission(permissionKey: String)
	fun resetPermission(permissionKey: String)
}