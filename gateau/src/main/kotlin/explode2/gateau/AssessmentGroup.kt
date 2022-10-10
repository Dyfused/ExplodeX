package explode2.gateau

interface AssessmentGroup {

	val id: String

	val name: String
	val assessmentIds: List<String>

	val assessments: Collection<Assessment>

	fun getAssessmentForMedal(medalLevel: Int): Assessment? {
		return assessments.singleOrNull { it.medalLevel == medalLevel }
	}

	fun setAssessmentForMedal(medalLevel: Int, assessment: Assessment)

	fun setAssessmentForMedal(
		medalLevel: Int,
		healthBarLength: Double,
		normalPassAccuracy: Double,
		goldenPassAccuracy: Double,
		exMissRate: Double,
		assessmentChartIds: List<String>
	)

	fun getAssessmentById(id: String) = assessments.first { it.id == id }

}