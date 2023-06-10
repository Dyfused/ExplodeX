package graphql.definition

data class ReviewRequest(
	val set: SetModel,
	val isUnranked: Boolean
)
