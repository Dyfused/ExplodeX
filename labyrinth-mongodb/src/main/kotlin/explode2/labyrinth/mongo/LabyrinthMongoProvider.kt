package explode2.labyrinth.mongo

import explode2.booster.config
import explode2.booster.instance
import explode2.labyrinth.*

class LabyrinthMongoProvider : LabyrinthProvider {

	private val provider: MongoManager

	init {
		val config = LabyrinthPlugin::class.java.instance!!.config
		val connString = config.getString("connection-string", "mongodb", "mongodb://localhost:27017", "数据库地址")
		val databaseName = config.getString("database-name", "mongodb", "Explode", "数据库名称")
		config.save()

		provider = MongoManager(LabyrinthMongoBuilder(connString, databaseName))
	}

	override val gameUserFactory: GameUserFactory
		get() = provider
	override val songSetFactory: SongSetFactory
		get() = provider
	override val songChartFactory: SongChartFactory
		get() = provider
	override val assessmentInfoFactory: AssessmentInfoFactory
		get() = provider
	override val gameRecordFactory: GameRecordFactory
		get() = provider
	override val assessmentRecordFactory: AssessmentRecordFactory
		get() = provider
}