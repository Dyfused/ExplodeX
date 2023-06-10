package graphql.definition

import graphql.schema.DataFetchingEnvironment

interface Self {

	suspend fun gotSet(env: DataFetchingEnvironment): List<SetModel>

	suspend fun assessmentRankSelf(env: DataFetchingEnvironment, assessmentGroupId: String?, medalLevel: Int?): AssessmentRecordWithRankModel?

	suspend fun playRankSelf(env: DataFetchingEnvironment, chartId: String?): PlayRecordWithRankModel?
}
