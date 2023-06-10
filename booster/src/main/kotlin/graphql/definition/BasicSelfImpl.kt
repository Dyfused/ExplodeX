package graphql.definition

import explode2.labyrinth.AssessmentInfoRepository
import explode2.labyrinth.AssessmentRecordRepository
import explode2.labyrinth.GameRecordRepository
import graphql.BasicMaze.getUser
import graphql.baah
import graphql.schema.DataFetchingEnvironment
import graphql.tunerize
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object BasicSelfImpl : Self, KoinComponent {

	private val assInfoRepo by inject<AssessmentInfoRepository>()
	private val assRecRepo by inject<AssessmentRecordRepository>()
	private val recRepo by inject<GameRecordRepository>()

	override suspend fun gotSet(env: DataFetchingEnvironment): List<SetModel> {
		val u = env.getUser().baah("invalid token")
		return u.ownedSets.map { it.tunerize(u) }
	}

	override suspend fun assessmentRankSelf(
		env: DataFetchingEnvironment,
		assessmentGroupId: String?,
		medalLevel: Int?
	): AssessmentRecordWithRankModel? {
		val u = env.getUser().baah("invalid token")
		val a = assInfoRepo
			.getAssessmentGroupById(assessmentGroupId.baah("invalid assessment group id"))
			.baah("assessment group not found").getAssessmentForMedal(medalLevel.baah("invalid medal level"))
			.baah("level not found")
		return assRecRepo.getPlayerBestAssessmentRecord(a.id, u.id)?.tunerize()
	}

	override suspend fun playRankSelf(env: DataFetchingEnvironment, chartId: String?): PlayRecordWithRankModel? {
		val u = env.getUser().baah("invalid token")
		return recRepo.getPlayerBestChartRecord(
			chartId.baah("invalid chart id"),
			u.id
		)
			?.tunerize()
	}
}