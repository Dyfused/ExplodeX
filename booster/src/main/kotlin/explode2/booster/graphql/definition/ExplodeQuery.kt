package explode2.booster.graphql.definition

import com.expediagroup.graphql.server.operations.Query
import explode2.booster.graphql.NonNegativeInt
import graphql.schema.DataFetchingEnvironment

interface ExplodeQuery : Query {

	suspend fun hello(env: DataFetchingEnvironment): String

	suspend fun gameSetting(): GameSettingModel

	suspend fun reviewer(env: DataFetchingEnvironment): Reviewer

	suspend fun set(
		env: DataFetchingEnvironment,
		playCountOrder: Int? = null,
		publishTimeOrder: Int? = null,
		limit: NonNegativeInt? = null,
		skip: NonNegativeInt? = null,
		isHidden: Int? = null,
		musicTitle: String? = null,
		isOfficial: Int? = null,
		isRanked: Int? = null
	): List<SetModel>

	suspend fun self(env: DataFetchingEnvironment): Self

	suspend fun assessmentGroup(env: DataFetchingEnvironment, limit: Int?, skip: Int?): List<AssessmentGroupModel>

	suspend fun assessmentRank(
		env: DataFetchingEnvironment,
		assessmentGroupId: String?,
		medalLevel: Int?,
		limit: NonNegativeInt?,
		skip: NonNegativeInt?
	): List<AssessmentRecordWithRankModel>

	suspend fun setById(env: DataFetchingEnvironment, _id: String?): SetModel

	suspend fun userByUsername(env: DataFetchingEnvironment, username: String?): UserModel?

	suspend fun playRank(env: DataFetchingEnvironment, chartId: String?, skip: NonNegativeInt?, limit: NonNegativeInt?): List<PlayRecordWithRankModel>

	suspend fun refreshSet(env: DataFetchingEnvironment, setVersion: List<ChartSetAndVersionInputModel>): List<RefreshSetModel>

}