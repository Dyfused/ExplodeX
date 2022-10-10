package explode2.booster.graphql.definition

import explode2.booster.Booster
import explode2.booster.graphql.*
import explode2.booster.graphql.getUser
import explode2.gateau.SongSet
import graphql.schema.DataFetchingEnvironment

object BasicSelfImpl : Self {
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
		val a = Booster.labyrinth.assessmentInfoFactory
			.getAssessmentGroupById(assessmentGroupId.baah("invalid assessment group id"))
			.baah("assessment group not found").getAssessmentForMedal(medalLevel.baah("invalid medal level"))
			.baah("level not found")
		return Booster.labyrinth.assessmentRecordFactory.getPlayerBestAssessmentRecord(a.id, u.id)?.tunerize()
	}

	override suspend fun playRankSelf(env: DataFetchingEnvironment, chartId: String?): PlayRecordWithRankModel? {
		val u = env.getUser().baah("invalid token")
		return Booster.labyrinth.gameRecordFactory.getPlayerBestChartRecord(chartId.baah("invalid chart id"), u.id)
			?.tunerize()
	}
}