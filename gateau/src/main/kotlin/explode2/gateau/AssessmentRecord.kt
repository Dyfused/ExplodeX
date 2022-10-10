package explode2.gateau

import java.time.OffsetDateTime

interface AssessmentRecord {

	val id: String

	val playerId: String
	val assessmentId: String

	val records: List<AssessmentRecordEntry>
	val exRecord: AssessmentRecordEntry?

	val sumScore: Int
		get() = records.sumOf { it.score } + (exRecord?.score ?: 0)
	val sumAccuracy: Double
		get() = (records.sumOf { it.accuracy } / 3.0) + (exRecord?.accuracy ?: 0.0)

	val uploadTime: OffsetDateTime

	val ranking: Int?

	val player: GameUser

	val result: Int

	data class AssessmentRecordEntry(
		val perfect: Int,
		val good: Int,
		val miss: Int,
		val score: Int,
		val accuracy: Double
	)
}