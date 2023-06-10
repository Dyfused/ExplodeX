package graphql.definition

import explode2.labyrinth.SearchCategory
import explode2.labyrinth.SearchSort
import explode2.labyrinth.SongSetRepository
import graphql.BasicMaze.getUser
import graphql.baah
import graphql.schema.DataFetchingEnvironment
import graphql.tunerize
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object BasicReviewerImpl : Reviewer, KoinComponent {

	private val songRepo by inject<SongSetRepository>()

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
		return songRepo
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