package graphql.definition

data class AssessmentRecordsModel(
	val achievementRate: Double, // Round in [0, 200]
	val isBest: Boolean,
	val playRecord: List<AssessmentPlayRecordModel>
)
