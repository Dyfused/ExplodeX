package explode2.gateau

interface Assessment {

	val id: String

	val medalLevel: Int

	val healthBarLength: Double
	val normalPassAccuracy: Double
	val goldenPassAccuracy: Double
	val exMissRate: Double

	val assessmentChartIds: List<String>
	val assessmentCharts: List<AssessmentChart>

	fun getAssessmentRecords(limit: Int, skip: Int): List<AssessmentRecord>
}