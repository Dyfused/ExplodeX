package graphql.definition

data class SetModel(
	val _id: String,
	var introduction: String,
	var coinPrice: Int,
	val noter: NoterModel,
	var musicTitle: String,
	var composerName: String,
	var playCount: Int,
	val chart: List<ChartModel>,
	var isGot: Boolean,
	var isRanked: Boolean,
)
