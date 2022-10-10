package explode2.booster.graphql.definition

import java.time.OffsetDateTime

data class BeforePlaySubmitModel(
	val PPTime: OffsetDateTime,
	val playingRecord: PlayingRecordModel
)
