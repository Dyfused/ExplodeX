package explode2.dynamite

import explode2.util.simplegraphql.graphql
import io.ktor.client.*

/**
 * Dynamite 等价 Graphql 请求
 */
class DynamiteRequests(private val url: String, private val soudayo: String? = null, private val http: HttpClient) {

	constructor(url: String, soudayo: String? = null, init: () -> HttpClient) : this(url, soudayo, init())

	// utilities

	private fun Boolean.toInt() = if(this) 1 else 0

	// actual requests

	suspend fun hello(): String {
		return http.graphql(url, "query { hello }")
	}

	suspend fun reviewSets(limit: Int, skip: Int, search: String): Map<String, Any> {
		val query = """
			|query reviewSets {
			|    r:reviewer {
			|        reviewRequest(limit: $limit, skip: $skip, status: 1, searchStr: "$search") {
			|            isUnranked
			|            set {
			|                _id
			|                introduction
			|                coinPrice
			|                isGot
			|                noter { username }
			|                musicTitle
			|                composerName
			|                chart{ _id difficultyClass difficultyValue }
			|            }
			|        }
			|    }
			|}
		""".trimMargin()
		return http.graphql(url, query, soudayo = soudayo)
	}

	suspend fun gotSet(): Map<String, Any> {
		val query = """
			|query {
			|    r:self {
			|        gotSet {
			|            _id
			|            introduction
			|            coinPrice
			|            isGot
			|            isRanked
			|            noter { username }
			|            musicTitle
			|            composerName
			|            playCount
			|            chart{ _id difficultyClass difficultyValue }
			|        }
			|    }
			|}
		""".trimMargin()
		return http.graphql(url, query, soudayo = soudayo)
	}

	suspend fun fetchSets(
		playCountOrder: Int,
		publishTimeOrder: Int,
		limit: Int,
		skip: Int,
		isHidden: Boolean,
		searchTitle: String,
		isOfficial: Boolean,
		isRanked: Boolean
	): Map<String, Any> {
		val query = """
			|query fetchSets {
			|    r:set(playCountOrder: $playCountOrder, publishTimeOrder: $publishTimeOrder, limit: $limit, skip: $skip,
			|        isHidden: ${isHidden.toInt()}, musicTitle: "$searchTitle", isOfficial: ${isOfficial.toInt()}, isRanked: ${if(isRanked) 1 else -1}) {
			|        _id
			|        introduction
			|        coinPrice
			|        isGot
			|        isRanked
			|        noter { username }
			|        musicTitle
			|        composerName
			|        playCount
			|        chart{ _id difficultyClass difficultyValue }
			|    }
			|}
		""".trimMargin()
		return http.graphql(url, query, soudayo = soudayo)
	}

}