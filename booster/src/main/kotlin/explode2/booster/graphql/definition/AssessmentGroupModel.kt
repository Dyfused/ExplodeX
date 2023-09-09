package explode2.booster.graphql.definition

data class AssessmentGroupModel(
	val _id: String,
	val name: String,
	val assessment: List<AssessmentModel>
)
