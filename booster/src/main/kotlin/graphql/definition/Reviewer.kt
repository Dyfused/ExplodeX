package graphql.definition

import graphql.schema.DataFetchingEnvironment

interface Reviewer {

	suspend fun reviewRequest(env: DataFetchingEnvironment, limit: Int?, skip: Int?, status: Int?, searchStr: String?): List<ReviewRequest>
}