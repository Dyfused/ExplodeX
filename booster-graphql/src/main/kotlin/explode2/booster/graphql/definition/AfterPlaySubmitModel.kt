package explode2.booster.graphql.definition

data class AfterPlaySubmitModel(
	val ranking: RankingModel,
	val RThisMonth: Int,
	val coin: Int?,
	val diamond: Int?
)
