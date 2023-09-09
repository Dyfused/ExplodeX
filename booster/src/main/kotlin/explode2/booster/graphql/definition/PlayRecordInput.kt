package explode2.booster.graphql.definition

data class PlayRecordInput(
	val mod: PlayModInput?,
	val isAlive: Boolean?,
	val score: Int?,
	val perfect: Int?,
	val good: Int?,
	val miss: Int?
)
