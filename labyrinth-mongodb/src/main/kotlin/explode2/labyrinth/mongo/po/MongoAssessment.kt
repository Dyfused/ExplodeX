package explode2.labyrinth.mongo.po

import org.bson.codecs.pojo.annotations.BsonId

data class MongoAssessment(
	@BsonId
	val id: String,
	val medalLevel: Int,
	val healthBarLength: Double,
	val normalPassAccuracy: Double,
	val goldenPassAccuracy: Double,
	val exMissRate: Double,
	val assessmentChartIds: List<String>
)
