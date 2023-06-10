package graphql.definition

import java.time.OffsetDateTime

data class AssessmentRecordWithRankModel(
	val player: PlayerModel,
	val rank: Int,
	val achievementRate: Double,
	val result: Int,
	val createTime: OffsetDateTime
)
