package explode2.labyrinth.mongo.po.aggregated

import org.bson.codecs.pojo.annotations.BsonId
import java.time.OffsetDateTime

data class MongoSongSetWithPlayCount(
	@BsonId
	val id: String,
	val musicName: String,
	val musicComposer: String,
	val introduction: String,
	val coinPrice: Int,
	val noterName: String,
	val noterUserId: String?,
	val chartIds: List<String>,
	val publishTime: OffsetDateTime,
	val category: Int,
	val isHidden: Boolean,
	val isReviewing: Boolean,
	val playCount: Int
)
