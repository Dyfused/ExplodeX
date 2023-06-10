package graphql.definition

data class AssessmentModel(
	val _id: String,
	val medalLevel: Int,
	val lifeBarLength: Double,
	val normalPassAcc: Double,
	val goldenPassAcc: Double,
	val exMiss: Double,
	val chart: List<AssessmentChartModel>,
	// A stupid design by Dynamite: They only read the 'best' entry but need to send a list of these shit to the client.
	// so in Explode, we only return a one-element list.
	val assessmentRecord: List<AssessmentRecordsModel>
)
