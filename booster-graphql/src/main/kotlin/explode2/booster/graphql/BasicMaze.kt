package explode2.booster.graphql

import com.google.common.cache.CacheBuilder
import explode2.booster.Booster.dispatchEvent
import explode2.booster.graphql.NonNegativeInt.Companion.int
import explode2.booster.graphql.definition.*
import explode2.booster.graphql.event.*
import explode2.gateau.*
import explode2.labyrinth.*
import explode2.labyrinth.LabyrinthPlugin.Companion.labyrinth
import graphql.schema.DataFetchingEnvironment
import thirdparty.goodbird.GoodBirdOracle
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object BasicMaze : ExplodeQuery, ExplodeMutation, MazeProvider {

	override val query: ExplodeQuery = this
	override val mutation: ExplodeMutation = this

	override suspend fun loginUser(env: DataFetchingEnvironment, username: String?, password: String?): UserModel {
		val event = UserLoginEvent(username.baah(), password.baah())
		if(event.isRejected()) {
			boom(event.getRejectReason())
		}
		val user = labyrinth.gameUserFactory.getGameUserByName(event.username)
		if(user != null) {
			if(user.validatePassword(event.password)) {
				return user.tunerize()
			} else {
				boom("Invalid username or password, please check before login.")
			}
		} else {
			boom("Invalid username, please check before login.")
		}
	}

	override suspend fun registerUser(env: DataFetchingEnvironment, username: String?, password: String?): UserModel {
		val user = labyrinth.gameUserFactory.getGameUserByName(username.baah())
		if(user == null) {
			val event = UserCreateEvent(username.baah(), password.baah()).dispatchEvent()
			if(event.isRejected()) {
				boom(event.getRejectReason())
			}
			return labyrinth.gameUserFactory.createGameUser(event.username, event.password).tunerize()
		} else {
			boom("Username existed")
		}
	}

	override suspend fun exchangeSet(env: DataFetchingEnvironment, setId: String?): ExchangeModel {
		val s = labyrinth.songSetFactory.getSongSetById(setId.baah()).baah()
		val u = env.getUser().baah()

		if(s.isUserGot(u)) { // prevent duplicating buying
			boom("You already had")
		}

		val event = BuySongEvent(u, s)
		if(s.coinPrice == 0 || s.coinPrice <= u.coin) {
			event.reject("Not Enough Money")
		}
		if(event.status == BuySongEvent.Status.ACCEPT) {
			u.coin -= s.coinPrice
			u.giveSet(s.id)
			return ExchangeModel(u.coin)
		} else {
			boom(event.message)
		}
	}

	private val assessmentSubmitCache = CacheBuilder.newBuilder().expireAfterWrite(10.minutes.toJavaDuration())
		.build<String, String>() // randomId -> assessmentId

	override suspend fun submitBeforeAssessment(
		env: DataFetchingEnvironment, assessmentGroupId: String?, medalLevel: Int?
	): BeforePlaySubmitModel {
		val u = env.getUser().baah("invalid user token")
		val assessment =
			labyrinth.assessmentInfoFactory.getAssessmentGroupById(assessmentGroupId.baah())
				.baah("Invalid assessment group!").getAssessmentForMedal(medalLevel.baah()).baah("Invalid medal level!")

		val rid = generateUUID()

		assessmentSubmitCache.put(rid, assessment.id)

		BeforeAssessmentEvent(u, assessment).dispatchEvent()

		return BeforePlaySubmitModel(OffsetDateTime.now(), PlayingRecordModel(rid))
	}

	override suspend fun submitAfterAssessment(
		env: DataFetchingEnvironment, playRecords: List<PlayRecordInput?>?, randomId: String?
	): AfterAssessmentModel {
		val u = env.getUser().baah("invalid user token")
		val ass = labyrinth.assessmentInfoFactory.getAssessmentById(
			assessmentSubmitCache.getIfPresent(randomId.baah("invalid random")).baah("random is expired")
		).baah("(Unreachable) assessment not found")
		val recs = playRecords.baah("playRecords").filterNotNull().map { (_, _, score, perfect, good, miss) ->
			val total = perfect.baah() + good.baah() + miss.baah()
			val accuracy = ((perfect.baah() * 100000L + good.baah() * 50000L) / total) / 1000.0

			AssessmentRecord.AssessmentRecordEntry(perfect.baah(), good.baah(), miss.baah(), score.baah(), accuracy)
		}.toMutableList()
		val exRec = if(recs.size > 3) recs.removeLast() else null

		val record = labyrinth.assessmentRecordFactory.createAssessmentRecord(
			ass.id, u.id, recs, exRec
		)

		AfterAssessmentEvent(u, ass, record).dispatchEvent()

		return AfterAssessmentModel(record.result, u.calculateR(), u.coin, u.diamond)
	}

	private val gameSubmitCache = CacheBuilder.newBuilder().expireAfterWrite(10.minutes.toJavaDuration())
		.build<String, String>() // randomId -> chartId

	override suspend fun submitBeforePlay(
		env: DataFetchingEnvironment, chartId: String?, PPCost: Int?, eventArgs: String?
	): BeforePlaySubmitModel {
		val u = env.getUser().baah("invalid user token")
		val c = labyrinth.songChartFactory.getSongChartById(chartId.baah("invalid chart id"))
			.baah("invalid chart")

		val rid = generateUUID()

		gameSubmitCache.put(rid, c.id)

		BeforePlayEvent(u, c).dispatchEvent()

		return BeforePlaySubmitModel(OffsetDateTime.now(), PlayingRecordModel(rid))
	}

	override suspend fun submitAfterPlay(
		env: DataFetchingEnvironment, randomId: String?, playRecord: PlayRecordInput?
	): AfterPlaySubmitModel {
		val u = env.getUser().baah("invalid user token")
		val c = labyrinth.songChartFactory
			.getSongChartById(gameSubmitCache.getIfPresent(randomId.baah("invalid random")).baah("random is expired"))
			.baah("(Unreachable) chart not found")
		val (_, _, score, perfect, good, miss) = playRecord.baah("invalid record")

		val accuracy = GoodBirdOracle.calculateAccuracy(perfect.baah(), good.baah(), miss.baah())
		val r = c.d?.let { GoodBirdOracle.calculateRank(accuracy, it) }

		u.coin += c.d?.let { GoodBirdOracle.calculateCoin(accuracy, it) } ?: 10

		labyrinth.gameRecordFactory.createGameRecord(
			c.id,
			u.id,
			perfect.baah(),
			good.baah(),
			miss.baah(),
			score.baah(),
			r
		)
		val record = labyrinth.gameRecordFactory.getPlayerBestChartRecord(c.id, u.id).baah()

		AfterPlayEvent(u, c, record).dispatchEvent()

		return AfterPlaySubmitModel(
			RankingModel(true, RankModel(record.ranking.baah())),
			u.calculateR(),
			u.coin,
			u.diamond
		)
	}

	override suspend fun hello(env: DataFetchingEnvironment): String {
		return labyrinth.javaClass.canonicalName
	}

	override suspend fun gameSetting(): GameSettingModel {
		return GameSettingModel(81)
	}

	override suspend fun reviewer(env: DataFetchingEnvironment): BasicReviewerImpl {
		return BasicReviewerImpl
	}

	override suspend fun set(
		env: DataFetchingEnvironment,
		playCountOrder: Int?,
		publishTimeOrder: Int?,
		limit: NonNegativeInt?,
		skip: NonNegativeInt?,
		isHidden: Int?,
		musicTitle: String?,
		isOfficial: Int?,
		isRanked: Int?
	): List<SetModel> {
		// 因为 refreshSet 导致的奇怪请求，直接返回空即可
		if(isOfficial == null) return listOf()

		val u = env.getUser()

		val cate = when {
			isHidden == 1 -> SearchCategory.HIDDEN
			isOfficial == 1 -> SearchCategory.OFFICIAL
			isRanked == 1 -> SearchCategory.RANKED
			isRanked == -1 -> SearchCategory.UNRANKED
			else -> SearchCategory.ALL
		}
		val sort = when {
			playCountOrder == -1 -> SearchSort.DESCENDING_BY_PLAY_COUNT
			publishTimeOrder == -1 -> SearchSort.DESCENDING_BY_PUBLISH_TIME
			else -> boom("invalid ordering")
		}

		return labyrinth.songSetFactory.searchSongSets(
			musicTitle,
			cate,
			sort,
			limit.int ?: 9,
			skip.int ?: 0
		)
			.map { it.tunerize(u) }
	}

	override suspend fun self(env: DataFetchingEnvironment): BasicSelfImpl {
		return BasicSelfImpl
	}

	override suspend fun assessmentGroup(
		env: DataFetchingEnvironment, limit: Int?, skip: Int?
	): List<AssessmentGroupModel> {
		val u = env.getUser().baah("invalid user token")
		return labyrinth.assessmentInfoFactory.getAssessmentGroups().map { it.tunerize(u) }
	}

	override suspend fun assessmentRank(
		env: DataFetchingEnvironment, assessmentGroupId: String?, medalLevel: Int?, limit: NonNegativeInt?, skip: NonNegativeInt?
	): List<AssessmentRecordWithRankModel> {
		return labyrinth.assessmentInfoFactory.getAssessmentGroupById(assessmentGroupId.baah("invalid group"))
			.baah("group not found").getAssessmentForMedal(medalLevel.baah("invalid medal level")).baah("invalid medal")
			.getAssessmentRecords(limit.int.baah("invalid limit"), skip.int.baah("invalid skip"))
			.map(AssessmentRecord::tunerize)
	}

	override suspend fun setById(env: DataFetchingEnvironment, _id: String?): SetModel {
		val u = env.getUser()
		return labyrinth.songSetFactory.getSongSetById(_id.baah("invalid id")).baah("set not found").tunerize(u)
	}

	override suspend fun userByUsername(env: DataFetchingEnvironment, username: String?): UserModel? {
		return labyrinth.gameUserFactory.getGameUserByName(username.baah("invalid username"))
			?.let(GameUser::tunerize)
	}

	override suspend fun playRank(
		env: DataFetchingEnvironment, chartId: String?, skip: NonNegativeInt?, limit: NonNegativeInt?
	): List<PlayRecordWithRankModel> {
		return labyrinth.gameRecordFactory
			.getChartRecords(
				chartId.baah("invalid chart id"),
				limit.int.baah("invalid limit"),
				skip.int.baah("invalid skip")
			)
			.map(GameRecord::tunerize)
	}

	override suspend fun refreshSet(
		env: DataFetchingEnvironment, setVersion: List<ChartSetAndVersionInputModel>
	): List<RefreshSetModel> {
		return setVersion.map { (setId, _) ->
			val s = labyrinth.songSetFactory.getSongSetById(setId).baah("invalid set: $setId")
			RefreshSetModel(s.id, s.state.isRanked, s.introduction, s.noterName, s.musicName)
		}
	}

	private fun generateUUID() = UUID.randomUUID().toString()

}

internal fun DataFetchingEnvironment.getUser(): GameUser? {
	return labyrinth.gameUserFactory.getGameUserById(graphQlContext.get("token"))
}