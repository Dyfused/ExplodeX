package explode2.booster.graphql.definition

import explode2.booster.Booster
import explode2.booster.graphql.*
import explode2.booster.graphql.getUser
import explode2.labyrinth.SearchCategory
import explode2.labyrinth.SearchSort
import graphql.schema.DataFetchingEnvironment

object BasicReviewerImpl : Reviewer {
	override suspend fun reviewRequest(
		env: DataFetchingEnvironment,
		limit: Int?,
		skip: Int?,
		status: Int?,
		searchStr: String?
	): List<ReviewRequest> {
		val u = env.getUser().baah("invalid token")
		if(!u.isReviewer) {
			return emptyList()
		}
		return Booster.labyrinth.songSetFactory
			.searchSongSets(
				searchStr,
				SearchCategory.REVIEW,
				SearchSort.DESCENDING_BY_PUBLISH_TIME,
				limit ?: 9,
				skip ?: 0
			).map {
				ReviewRequest(it.tunerize(u), it.state.isUnRanked)
			}
	}
}