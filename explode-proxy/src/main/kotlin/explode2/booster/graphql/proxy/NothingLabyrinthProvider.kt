package explode2.booster.graphql.proxy

import explode2.labyrinth.*

/**
 * 用于代理服务器的 LabyrinthProvider，
 * 因为代理服务器不需要数据库，所以这里的所有内容提供 API 都不需要实现。
 */
class NothingLabyrinthProvider : LabyrinthProvider {

	override val gameUserFactory: GameUserFactory
		get() = TODO("Not yet implemented")
	override val songSetFactory: SongSetFactory
		get() = TODO("Not yet implemented")
	override val songChartFactory: SongChartFactory
		get() = TODO("Not yet implemented")
	override val assessmentInfoFactory: AssessmentInfoFactory
		get() = TODO("Not yet implemented")
	override val gameRecordFactory: GameRecordFactory
		get() = TODO("Not yet implemented")
	override val assessmentRecordFactory: AssessmentRecordFactory
		get() = TODO("Not yet implemented")
}