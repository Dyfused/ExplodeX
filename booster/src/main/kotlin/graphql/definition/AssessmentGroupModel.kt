package graphql.definition

data class AssessmentGroupModel(
	val _id: String,
	val name: String,
	val assessment: List<AssessmentModel>
)
