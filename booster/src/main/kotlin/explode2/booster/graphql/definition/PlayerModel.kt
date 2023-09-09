package explode2.booster.graphql.definition

data class PlayerModel(
	val _id: String,
	val username: String,
	val highestGoldenMedalLevel: Int?,
	val RThisMonth: Int
)
