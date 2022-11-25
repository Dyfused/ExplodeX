package explode2.booster.bomb.submods

import explode2.booster.bomb.logger
import explode2.booster.bomb.superstarMarker
import explode2.gateau.*
import io.ktor.server.auth.*
import java.time.OffsetDateTime

interface BombPrincipal : Principal {

	val user: GameUser?
}

object SuperstarPrincipal : BombPrincipal {
	// 假玩家数据
	override val user: GameUser = object : GameUser {
		override val id: String = "00000000-0000-0000-0000-000000000000"
		override var username: String = "SUPERSTAR"
		override var coin: Int = Int.MAX_VALUE
		override var diamond: Int = Int.MAX_VALUE
		override var ppTime: OffsetDateTime = OffsetDateTime.MIN
		override var isReviewer: Boolean = true
		override val SongSet.isOwned: Boolean get() = true
		override val ownedSets: List<SongSet> = listOf()
		override fun calculateR(): Int = Int.MIN_VALUE
		override fun calculateHighestGoldenMedal(): Int = 0
		override fun changePassword(password: String) {
			logger.warn(superstarMarker, "Unexpected \"changePassword($password)\" has been invoked!")
		}
		override fun validatePassword(password: String): Boolean {
			logger.warn(superstarMarker, "Unexpected \"validatePassword($password)\" has been invoked!")
			return false
		}
		override fun giveSet(setId: String) {
			logger.warn(superstarMarker, "Unexpected \"giveSet($setId)\" has been invoked!")
		}
		override fun calculateLastRecords(limit: Int): List<GameRecord> = emptyList()
		override fun calculateBestRecords(limit: Int, sortedBy: ScoreOrRanking): List<GameRecord> = emptyList()
        override fun getAllRecords(limit: Int, skip: Int): List<GameRecord> = emptyList()
	}
}