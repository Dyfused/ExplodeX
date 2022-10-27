package explode2.labyrinth.mongo.po

import org.bson.codecs.pojo.annotations.BsonId
import java.time.OffsetDateTime

data class MongoSongSet(
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
	/**
	 * UnRanked - 0
	 * Ranked - 1
	 * Official - 2
	 *
	 * @see explode2.gateau.SongState
	 */
	val category: Int,
	val hidden: Boolean,
	val reviewing: Boolean,
)