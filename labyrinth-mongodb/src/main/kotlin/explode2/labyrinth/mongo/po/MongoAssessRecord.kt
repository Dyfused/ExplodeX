package explode2.labyrinth.mongo.po

import org.bson.codecs.pojo.annotations.BsonId
import java.time.OffsetDateTime

data class MongoAssessRecord(
	@BsonId
	val id: String,
	val playerId: String,
	val assessmentId: String,
	val records: List<AssessmentRecordDetail>,
	val exRecord: AssessmentRecordDetail?,
	val uploadTime: OffsetDateTime,
)