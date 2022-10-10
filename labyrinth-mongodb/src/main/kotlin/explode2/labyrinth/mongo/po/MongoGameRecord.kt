package explode2.labyrinth.mongo.po

import org.bson.codecs.pojo.annotations.BsonId
import java.time.OffsetDateTime

data class MongoGameRecord(
	@BsonId
	val id: String,
	val playerId: String,
	val playedChartId: String,
	val score: Int,
	val detail: RecordDetail,
	val uploadTime: OffsetDateTime,
	val r: Int?,
)
