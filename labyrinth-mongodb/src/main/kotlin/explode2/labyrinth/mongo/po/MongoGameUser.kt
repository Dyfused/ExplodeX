package explode2.labyrinth.mongo.po

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.OffsetDateTime

data class MongoGameUser(
	@BsonId

	val id: ObjectId,
	val username: String,
	val password: String,
	val coin: Int,
	val diamond: Int,
	val ppTime: OffsetDateTime,
	val isReviewer: Boolean,
	val ownedSongSetIds: List<String>,
	val permissionGranted: List<String>? = listOf(),
	val permissionRevoked: List<String>? = listOf(),
)
