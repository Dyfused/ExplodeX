package explode2.booster.graphql.definition

import java.time.OffsetDateTime

data class PlayRecordWithRankModel(
	val player: PlayerModel,
	val mod: PlayMod,
	val rank: Int,
	val score: Int,
	val perfect: Int,
	val good: Int,
	val miss: Int,
	val createTime: OffsetDateTime
)
