package explode2.labyrinth

interface LabyrinthProvider {

	val gameUserRepository: GameUserRepository
	val songSetRepository: SongSetRepository
	val songChartRepository: SongChartRepository
	val assessmentInfoRepository: AssessmentInfoRepository
	val gameRecordRepository: GameRecordRepository
	val assessmentRecordRepository: AssessmentRecordRepository
}