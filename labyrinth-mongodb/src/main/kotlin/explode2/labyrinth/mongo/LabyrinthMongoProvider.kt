package explode2.labyrinth.mongo

import com.electronwill.nightconfig.core.file.FileConfig
import explode2.labyrinth.*
import kotlin.io.path.Path

class LabyrinthMongoProvider : LabyrinthProvider {

	private val provider: MongoManager

	init {
		val config = FileConfig.builder(Path("labyrinth.mongodb.config.toml"))
			.autosave()
			.autoreload()
			.charset(Charsets.UTF_8)
			.build()

		val connString = config.getOrElse("connection-string", "mongodb://localhost:27017")
		val databaseName = config.getOrElse("database-name", "Explode")

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