package explode2.booster.graphql.definition

import explode2.booster.graphql.BasicMaze.getUser
import explode2.booster.graphql.BasicMaze.refreshRankingListInHour
import explode2.booster.graphql.GraphqlDataSource.assInfoRepo
import explode2.booster.graphql.GraphqlDataSource.assRecRepo
import explode2.booster.graphql.GraphqlDataSource.recRepo
import explode2.booster.graphql.RefreshingRankingList
import explode2.booster.graphql.baah
import explode2.booster.graphql.tunerize
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
		val a = assInfoRepo
			.getAssessmentGroupById(assessmentGroupId.baah("invalid assessment group id"))
			.baah("assessment group not found").getAssessmentForMedal(medalLevel.baah("invalid medal level"))
			.baah("level not found")
		return assRecRepo.getPlayerBestAssessmentRecord(a.id, u.id)?.tunerize()
	}

	override suspend fun playRankSelf(env: DataFetchingEnvironment, chartId: String?): PlayRecordWithRankModel? {
		val u = env.getUser().baah("invalid token")
		val cid = chartId.baah("invalid chart id")
		return if(refreshRankingListInHour > 0) {
			RefreshingRankingList.getOrCreate(cid, refreshRankingListInHour, recRepo).get(u.id)
		} else if(refreshRankingListInHour < 0) {
			null
		} else {
			recRepo.getPlayerBestChartRecord(cid, u.id)?.tunerize()
		}
	}
}