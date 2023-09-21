package explode2.labyrinth.mongo.po.aggregated

import explode2.labyrinth.mongo.po.AssessmentRecordDetail
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.OffsetDateTime

data class MongoAssessRecordRanked(
	@BsonId
	val id: String,
	val playerId: ObjectId,
	val assessmentId: String,
	val records: List<AssessmentRecordDetail>,
	val exRecord: AssessmentRecordDetail?,
	val uploadTime: OffsetDateTime,
	val ranking: Int?
)