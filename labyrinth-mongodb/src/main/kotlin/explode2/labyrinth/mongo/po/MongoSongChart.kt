package explode2.labyrinth.mongo.po

import org.bson.codecs.pojo.annotations.BsonId

data class MongoSongChart(
	@BsonId
	val id: String,
	val difficultyClass: Int,
	val difficultyValue: Int,
	val D: Double?
)