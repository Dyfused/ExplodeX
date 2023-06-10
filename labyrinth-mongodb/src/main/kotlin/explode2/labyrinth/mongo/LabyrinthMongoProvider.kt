package explode2.labyrinth.mongo

import explode2.booster.ExplodeConfig
import explode2.labyrinth.*

class LabyrinthMongoProvider : LabyrinthProvider {

	private val provider: MongoManager

	init {
		val config = ExplodeConfig.get("labyrinth")
		// 优先使用环境变量
		val connString =
			System.getenv("DB_URL") ?: config.getString(
				"connection-string",
				"mongodb",
				"mongodb://localhost:27017",
				"数据库地址"
			)
		val databaseName =
			System.getenv("DB_NAME") ?: config.getString("database-name", "mongodb", "Explode", "数据库名称")
		config.save()

		provider = MongoManager(LabyrinthMongoBuilder(connString, databaseName))
	}

	override val gameUserRepository: GameUserRepository
		get() = provider
	override val songSetRepository: SongSetRepository
		get() = provider
	override val songChartRepository: SongChartRepository
		get() = provider
	override val assessmentInfoRepository: AssessmentInfoRepository
		get() = provider
	override val gameRecordRepository: GameRecordRepository
		get() = provider
	override val assessmentRecordRepository: AssessmentRecordRepository
		get() = provider
}