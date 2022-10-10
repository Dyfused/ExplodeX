package explode2.booster.graphql.definition

import com.expediagroup.graphql.server.operations.Mutation
import graphql.schema.DataFetchingEnvironment

interface ExplodeMutation : Mutation {

	suspend fun loginUser(env: DataFetchingEnvironment, username: String?, password: String?): UserModel

	suspend fun registerUser(env: DataFetchingEnvironment, username: String?, password: String?): UserModel

	suspend fun exchangeSet(env: DataFetchingEnvironment, setId: String?): ExchangeModel

	suspend fun submitBeforeAssessment(env: DataFetchingEnvironment, assessmentGroupId: String?, medalLevel: Int?): BeforePlaySubmitModel

	suspend fun submitAfterAssessment(env: DataFetchingEnvironment, playRecords: List<PlayRecordInput?>?, randomId: String?): AfterAssessmentModel

	suspend fun submitBeforePlay(env: DataFetchingEnvironment, chartId: String?, PPCost: Int?, eventArgs: String?): BeforePlaySubmitModel

	suspend fun submitAfterPlay(env: DataFetchingEnvironment, randomId: String?, playRecord: PlayRecordInput?): AfterPlaySubmitModel
}