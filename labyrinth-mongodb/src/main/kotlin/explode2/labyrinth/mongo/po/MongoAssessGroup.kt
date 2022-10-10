package explode2.labyrinth.mongo.po

import explode2.labyrinth.mongo.createNewRandomUUID
import org.bson.codecs.pojo.annotations.BsonId

data class MongoAssessGroup(
	@BsonId
	val id: String,
	val name: String,
	// 契合单例模式，为 true 时作为单例
	val selected: Boolean,
	val assessments: Map<String, MongoAssessment>
) {
	companion object {
		val EMPTY = MongoAssessGroup(
			createNewRandomUUID(),
			"Empty Template",
			true,
			mapOf()
		)
	}
}
