package explode2.labyrinth

import java.util.*

interface LabyrinthProvider {

	val gameUserFactory: GameUserFactory
	val songSetFactory: SongSetFactory
	val songChartFactory: SongChartFactory
	val assessmentInfoFactory: AssessmentInfoFactory
	val gameRecordFactory: GameRecordFactory
	val assessmentRecordFactory: AssessmentRecordFactory

	companion object {

		private val providerCache: LabyrinthProvider by lazy {
			ServiceLoader.load(LabyrinthProvider::class.java).findFirst().get()
		}

		fun getProvider(): LabyrinthProvider = providerCache
	}

}