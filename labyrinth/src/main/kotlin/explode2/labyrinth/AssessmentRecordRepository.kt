package explode2.labyrinth

import explode2.gateau.AssessmentRecord

interface AssessmentRecordRepository {

	fun getAssessmentRecords(
		assessmentId: String,
		limit: Int = 20,
		skip: Int = 0
	): List<AssessmentRecord>

	fun getPlayerBestAssessmentRecord(
		assessmentId: String,
		playerId: String
	): AssessmentRecord?

	fun createAssessmentRecord(
		assessmentId: String,
		playerId: String,
		records: List<AssessmentRecord.AssessmentRecordEntry>,
		exRecord: AssessmentRecord.AssessmentRecordEntry?,
		result: Int? = null,
		id: String? = null
	): AssessmentRecord

}