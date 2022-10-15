package explode2.labyrinth

/**
 * @see LabyrinthPlugin
 */
interface LabyrinthProvider {

	val gameUserFactory: GameUserFactory
	val songSetFactory: SongSetFactory
	val songChartFactory: SongChartFactory
	val assessmentInfoFactory: AssessmentInfoFactory
	val gameRecordFactory: GameRecordFactory
	val assessmentRecordFactory: AssessmentRecordFactory
}