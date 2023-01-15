package explode2.labyrinth.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.*
import explode2.booster.Booster.dispatchEvent
import explode2.gateau.*
import explode2.labyrinth.*
import explode2.labyrinth.event.SongCreatedEvent
import explode2.labyrinth.mongo.po.*
import explode2.labyrinth.mongo.po.aggregated.*
import explode2.labyrinth.util.StringProcessor
import org.bson.conversions.Bson
import org.litote.kmongo.*
import java.lang.Character.UnicodeScript
import java.time.OffsetDateTime
import java.util.*
import java.util.regex.PatternSyntaxException

class MongoManager(private val provider: LabyrinthMongoBuilder = LabyrinthMongoBuilder()) : SongSetFactory,
	SongChartFactory,
	GameUserFactory, GameRecordFactory, AssessmentRecordFactory, AssessmentInfoFactory {

	private val collSet = provider.getDatabase().getCollection<MongoSongSet>("Sets")
	private val collChart = provider.getDatabase().getCollection<MongoSongChart>("Charts")
	private val collUser = provider.getDatabase().getCollection<MongoGameUser>("Users")
	private val collGameRec = provider.getDatabase().getCollection<MongoGameRecord>("GameRecords")
	private val collAssessRec = provider.getDatabase().getCollection<MongoAssessRecord>("AssessRecords")
	private val collAssessInfo = provider.getDatabase().getCollection<MongoAssessGroup>("AssessInfo")

	// SONG SET

	private fun updateMongoSongSet(id: String, value: MongoSongSet): MongoSongSet {
		collSet.updateOneById(id, value, upsert())
		return collSet.findOneById(id).nn()
	}

	override fun getSongSetById(id: String): SongSet? {
		return collSet.findOneById(id)?.let(::SongSetDelegate)
	}

	override fun getSongSetByMusic(musicName: String): SongSet? {
		return collSet.findOne(MongoSongSet::musicName eq musicName)?.let(::SongSetDelegate)
	}

	override fun getSongSetByChart(chartId: String): SongSet? {
		return collSet.findOne(MongoSongSet::chartIds contains chartId)?.let(::SongSetDelegate)
	}

	override fun createSongSet(
		musicName: String,
		musicComposer: String,
		introduction: String,
		noterName: String,
		charts: List<String>,
		state: SongState,
		id: String?,
		coinPrice: Int,
		publishTime: OffsetDateTime,
		noterUser: GameUser?
	): SongSet {
		// to validate the chart and used for event
		val validatedChart = charts.map { getSongChartById(it) ?: error("Invalid chart not found in Mongo: $it") }

		val actualId = id ?: createNewRandomSetId()
		val s = MongoSongSet(
			actualId,
			musicName,
			musicComposer,
			introduction,
			coinPrice,
			noterName,
			noterUser?.id,
			charts,
			publishTime,
			state.category,
			state.isHidden,
			state.isReviewing
		)
		val set = SongSetDelegate(updateMongoSongSet(actualId, s))

		SongCreatedEvent(set, validatedChart).dispatchEvent()

		return set
	}

	override fun searchSongSets(
		matchingName: String?,
		matchingCategory: SearchCategory?,
		sortBy: SearchSort?,
		limit: Int,
		skip: Int
	): Collection<SongSet> {
		require(limit < 500) { "too much content requested" }

		class SongSetWithCharts(val charts: List<MongoSongChart>)
		class SongSetWithPlayCount(val playCount: Int)

		val pipeline: MutableList<Bson> = mutableListOf()

		if(!matchingName.isNullOrBlank()) {
			val proc = StringProcessor(matchingName)

			when {
				// 正则匹配名字
				proc.consume("r:") -> {
					kotlin.runCatching {
						val regex = Regex(proc.remaining)
						pipeline += match(MongoSongSet::musicName.regex(regex))
					}.onFailure { // 处理正则错误
						if(it is PatternSyntaxException) {
							error("failed to compile regex pattern!")
						} else {
							throw it
						}
					}
				}

				// 匹配难度
				proc.consume("h:") -> {
					// 把查询谱面信息的 pipeline 加进来
					pipeline += lookup("Charts", "chartIds", "_id", "charts")

					when {
						proc.consume("==") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::difficultyValue eq proc.remainingAsInt))
						}

						proc.consume(">=") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::difficultyValue gte proc.remainingAsInt))
						}

						proc.consume("<=") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::difficultyValue lte proc.remainingAsInt))
						}

						proc.consume(">") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::difficultyValue gt proc.remainingAsInt))
						}

						proc.consume("<") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::difficultyValue lt proc.remainingAsInt))
						}

						proc.consume("!=") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::difficultyValue ne proc.remainingAsInt))
						}

						else -> {
							error("invalid matching syntax")
						}
					}
				}

				// 匹配谱面数量
				proc.consume("cc:") -> {
					// 把查询谱面信息的 pipeline 加进来
					pipeline += lookup("Charts", "chartIds", "_id", "charts")

					val count = proc.remainingAsInt ?: error("invalid count number!")
					pipeline += match(SongSetWithCharts::charts size count)
				}

				// 匹配定数
				proc.consume("d:") -> {
					// 把查询谱面信息的 pipeline 加进来
					pipeline += lookup("Charts", "chartIds", "_id", "charts")

					when {
						proc.consume("==") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::d eq proc.remainingAsDouble))
						}

						proc.consume(">=") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::d gte proc.remainingAsDouble))
						}

						proc.consume("<=") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::d lte proc.remainingAsDouble))
						}

						proc.consume(">") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::d gt proc.remainingAsDouble))
						}

						proc.consume("<") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::d lt proc.remainingAsDouble))
						}

						proc.consume("!=") -> {
							pipeline += match(SongSetWithCharts::charts elemMatch (MongoSongChart::d ne proc.remainingAsDouble))
						}
					}
				}

				// 默认模糊查询名字
				else -> {
					pipeline += match(MongoSongSet::musicName.regex(matchingName, "i"))
				}
			}
		}

		if(matchingCategory != null) {
			// 限定隐藏
			pipeline += if(matchingCategory == SearchCategory.HIDDEN) {
				match(MongoSongSet::hidden eq true)
			} else {
				match(MongoSongSet::hidden eq false)
			}

			// 限定审核
			pipeline += if(matchingCategory == SearchCategory.REVIEW) {
				match(MongoSongSet::reviewing eq true)
			} else {
				match(MongoSongSet::reviewing eq false)
			}

			// 其他
			when(matchingCategory) {
				SearchCategory.OFFICIAL -> {
					pipeline += match(MongoSongSet::category eq 2)
				}

				SearchCategory.RANKED -> {
					pipeline += match(or(MongoSongSet::category eq 1, MongoSongSet::category eq 2))
				}

				SearchCategory.UNRANKED -> {
					pipeline += match(MongoSongSet::category eq 0)
				}

				else -> {}
			}
		}

		// 排序顺序
		when(sortBy) {
			null, SearchSort.DESCENDING_BY_PUBLISH_TIME -> {
				pipeline += sort(descending(MongoSongSet::publishTime))
			}

			// FIXME: 修复性能问题
			SearchSort.DESCENDING_BY_PLAY_COUNT -> {
				// 添加游玩次数查询
				pipeline += lookup("GameRecords", "chartIds", "playedChartId", "playRecords")
				pipeline += addFields(Field(
					"playCount",
					MongoOperator.size.from("\$playRecords")
				))
				pipeline += sort(descending(SongSetWithPlayCount::playCount))
			}
		}

		// 添加限制
		pipeline += skip(skip)
		pipeline += limit(limit)

		return collSet.aggregate<MongoSongSet>(*pipeline.toTypedArray()).map(::SongSetDelegate).toList()
	}

	// SONG CHART

	private fun updateMongoSongChart(id: String, value: MongoSongChart): MongoSongChart {
		collChart.updateOneById(id, value, upsert())
		return collChart.findOneById(id).nn()
	}

	private fun getSongChartPlayCount(chartId: String): Int? {
		collChart.findOneById(chartId) ?: return null
		return collGameRec.countDocuments(MongoGameRecord::playedChartId eq chartId).toInt()
	}

	override fun getSongChartById(id: String): SongChart? {
		return collChart.findOneById(id)?.let(::SongChartDelegate)
	}

	override fun createSongChart(
		difficultyClass: Int,
		difficultyValue: Int,
		id: String?,
		d: Double?
	): SongChart {
		val actualId = id ?: createNewRandomChartId()
		return SongChartDelegate(
			updateMongoSongChart(
				actualId,
				MongoSongChart(actualId, difficultyClass, difficultyValue, d)
			)
		)
	}

	// GAME USER

	private fun updateMongoGameUser(id: String, value: MongoGameUser): MongoGameUser {
		collUser.updateOneById(id, value, upsert())
		return collUser.findOneById(id).nn()
	}

	override fun getGameUserById(id: String): GameUser? {
		return collUser.findOneById(id)?.let(::GameUserDelegate)
	}

	override fun getGameUserByName(username: String): GameUser? {
		return collUser.findOne(MongoGameUser::username eq username)?.let(::GameUserDelegate)
	}

	private fun String.isValid(): Boolean {
		return codePoints().allMatch { UnicodeScript.of(it) != UnicodeScript.UNKNOWN }
	}

	override fun createGameUser(username: String, password: String, id: String?): GameUser {
		require(username.isValid()) { "username contains invalid characters" }
		require(password.isValid()) { "password contains invalid characters" }

		val actualId = id ?: createNewRandomUUID()
		val u = MongoGameUser(
			actualId,
			username,
			password,
			0,
			0,
			OffsetDateTime.now(),
			false,
			listOf()
		)
		return GameUserDelegate(updateMongoGameUser(actualId, u))
	}

	// GAME RECORD

	private fun updateMongoGameRecord(id: String, value: MongoGameRecord): MongoGameRecord {
		collGameRec.updateOneById(id, value, upsert())
		return collGameRec.findOneById(id).nn()
	}

	override fun getChartRecords(
		chartId: String,
		limit: Int,
		skip: Int
	): Collection<GameRecord> {
		data class MiddleObject<T>(val data: T)

		return collGameRec.aggregate<MongoGameRecordRanked>(
			match(MongoGameRecord::playedChartId eq chartId),
			sort(
				descending(
					MongoGameRecord::playedChartId,
					MongoGameRecord::playerId,
					MongoGameRecord::score,
					MongoGameRecord::uploadTime
				)
			),
			group(MongoGameRecord::playerId, Accumulators.first("data", ThisDocument)),
			Aggregates.replaceWith(MiddleObject<MongoGameRecord>::data),
			Aggregates.setWindowFields(null, descending(MongoGameRecord::score), WindowOutputFields.rank("ranking")),
			skip(skip),
			limit(limit)
		).map(::GameRecordRankedWrap).toList()
	}

	override fun getPlayerBestChartRecord(chartId: String, playerId: String): GameRecord? {
		data class MiddleObject<T>(val data: T)

		return collGameRec.aggregate<MongoGameRecordRanked>(
			match(MongoGameRecord::playedChartId eq chartId),
			sort(
				descending(
					MongoGameRecord::playedChartId,
					MongoGameRecord::playerId,
					MongoGameRecord::score,
					MongoGameRecord::uploadTime
				)
			),
			group(MongoGameRecord::playerId, Accumulators.first("data", ThisDocument)),
			Aggregates.replaceWith(MiddleObject<MongoGameRecord>::data),
			Aggregates.setWindowFields(null, descending(MongoGameRecord::score), WindowOutputFields.rank("ranking")),
			match(MongoGameRecord::playerId eq playerId)
		).firstOrNull()?.let(::GameRecordRankedWrap)
	}

	override fun createGameRecord(
		chartId: String,
		playerId: String,
		perfect: Int,
		good: Int,
		miss: Int,
		score: Int,
		r: Int?,
		id: String?
	): GameRecord {
		val actualId = id ?: createNewRandomUUID()
		val rec = MongoGameRecord(
			actualId,
			playerId,
			chartId,
			score,
			RecordDetail(perfect, good, miss),
			OffsetDateTime.now(),
			r
		)
		return GameRecordWrap(updateMongoGameRecord(actualId, rec))
	}

	// ASSESSMENT RECORD

	private fun updateMongoAssessRecord(id: String, value: MongoAssessRecord): MongoAssessRecord {
		collAssessRec.updateOneById(id, value, upsert())
		return collAssessRec.findOneById(id).nn()
	}

	override fun getAssessmentRecords(assessmentId: String, limit: Int, skip: Int): List<AssessmentRecord> {
		class MiddleObject(val data: MongoAssessRecord, val sumAccuracy: Double)

		return collAssessRec.aggregate<MongoAssessRecordRanked>(
			match(MongoAssessRecord::assessmentId eq assessmentId),
			addFields(
				Field(
					"sumAccuracy",
					MongoOperator.add.from(listOf("\$exRecord.accuracy", MongoOperator.avg.from("\$records.accuracy")))
				)
			),
			sort(
				descending(
					MongoAssessRecord::assessmentId,
					MongoAssessRecord::playerId,
					MiddleObject::sumAccuracy,
					MongoAssessRecord::uploadTime
				)
			),
			group(MongoAssessRecord::playerId, Accumulators.first("data", ThisDocument)),
			Aggregates.replaceWith(MiddleObject::data),
			Aggregates.setWindowFields(null, descending(MiddleObject::sumAccuracy), WindowOutputFields.rank("ranking")),
			skip(skip),
			limit(limit)
		).map(::AssessmentRecordRankedWrap).toList()
	}

	override fun getPlayerBestAssessmentRecord(assessmentId: String, playerId: String): AssessmentRecord? {
		class MiddleObject(val data: MongoAssessRecord, val sumAccuracy: Double)

		return collAssessRec.aggregate<MongoAssessRecordRanked>(
			match(MongoAssessRecord::assessmentId eq assessmentId),
			addFields(
				Field(
					"sumAccuracy",
					MongoOperator.add.from(listOf("\$exRecord.accuracy", MongoOperator.avg.from("\$records.accuracy")))
				)
			),
			sort(
				descending(
					MongoAssessRecord::assessmentId,
					MongoAssessRecord::playerId,
					MiddleObject::sumAccuracy,
					MongoAssessRecord::uploadTime
				)
			),
			group(MongoAssessRecord::playerId, Accumulators.first("data", ThisDocument)),
			Aggregates.replaceWith(MiddleObject::data),
			Aggregates.setWindowFields(null, descending(MiddleObject::sumAccuracy), WindowOutputFields.rank("ranking")),
			match(MongoAssessRecord::playerId eq playerId)
		).firstOrNull()?.let(::AssessmentRecordRankedWrap)
	}

	override fun createAssessmentRecord(
		assessmentId: String,
		playerId: String,
		records: List<AssessmentRecord.AssessmentRecordEntry>,
		exRecord: AssessmentRecord.AssessmentRecordEntry?,
		result: Int?,
		id: String?
	): AssessmentRecord {
		val actualId = id ?: createNewRandomUUID()
		val rec = MongoAssessRecord(
			actualId,
			playerId,
			assessmentId,
			records.map { AssessmentRecordDetail(it.perfect, it.good, it.miss, it.score, it.accuracy) },
			exRecord?.let { AssessmentRecordDetail(it.perfect, it.good, it.miss, it.score, it.accuracy) },
			OffsetDateTime.now()
		)

		return AssessmentRecordWrap(updateMongoAssessRecord(actualId, rec))
	}

	// ASSESSMENT INFO

	private fun updateMongoAssessmentGroup(id: String, value: MongoAssessGroup): MongoAssessGroup {
		collAssessInfo.updateOneById(id, value, upsert())
		return collAssessInfo.findOneById(id).nn()
	}

	@Deprecated("Use getAssessmentGroups", replaceWith = ReplaceWith("getAssessmentGroups()[0]"))
	override val singletonAssessmentGroup: AssessmentGroup
		get() = (collAssessInfo.findOne(MongoAssessGroup::selected eq true)
			?: MongoAssessGroup.EMPTY).let(::AssessmentGroupDelegate)

	override fun getAssessmentGroupById(gId: String): AssessmentGroup? {
		return collAssessInfo.findOneById(gId)?.let(::AssessmentGroupDelegate)
	}

	override fun getAssessmentById(id: String): Assessment? {
		return collAssessInfo.findOne(MongoAssessGroup::assessments / MongoAssessment::id eq id)
			?.let(::AssessmentGroupDelegate)?.getAssessmentById(id)
	}

	// region DELEGATES

	inner class SongSetDelegate(private var delegate: MongoSongSet) : SongSet {
		override val id: String
			get() = delegate.id
		override var musicName: String
			get() = delegate.musicName
			set(value) {
				delegate = updateMongoSongSet(id, delegate.copy(musicName = value))
			}
		override var musicComposer: String
			get() = delegate.musicComposer
			set(value) {
				delegate = updateMongoSongSet(id, delegate.copy(musicComposer = value))
			}
		override var introduction: String
			get() = delegate.introduction
			set(value) {
				delegate = updateMongoSongSet(id, delegate.copy(introduction = value))
			}
		override var coinPrice: Int
			get() = delegate.coinPrice
			set(value) {
				delegate = updateMongoSongSet(id, delegate.copy(coinPrice = value))
			}
		override var noterName: String
			get() = delegate.noterName
			set(value) {
				delegate = updateMongoSongSet(id, delegate.copy(noterName = value))
			}
		override var noterUserId: String?
			get() = delegate.noterUserId
			set(value) {
				delegate = updateMongoSongSet(id, delegate.copy(noterUserId = value))
			}
		override var chartIds: List<String>
			get() = delegate.chartIds
			set(value) {
				delegate = updateMongoSongSet(id, delegate.copy(chartIds = value))
			}
		override val charts: List<SongChart>
			get() = delegate.chartIds.mapNotNull(collChart::findOneById).map(::SongChartDelegate)
		override val playCount: Int
			get() = getSongChartPlayCount(id) ?: 0
		override val publishTime: OffsetDateTime
			get() = delegate.publishTime
		override val state: SongState
			get() = object : SongState {
				override var category: Int
					get() = delegate.category
					set(value) {
						delegate = updateMongoSongSet(id, delegate.copy(category = value))
					}
				override var isHidden: Boolean
					get() = delegate.hidden
					set(value) {
						delegate = updateMongoSongSet(id, delegate.copy(hidden = value))
					}
				override var isReviewing: Boolean
					get() = delegate.reviewing
					set(value) {
						delegate = updateMongoSongSet(id, delegate.copy(reviewing = value))
					}
			}

		override fun isUserGot(user: GameUser): Boolean {
			return with(user) { isOwned }
		}

		override fun toString(): String = delegate.toString()
	}

	inner class SongChartDelegate(private var delegate: MongoSongChart) : SongChart {
		override val id: String
			get() = delegate.id
		override var difficultyClass: Int
			get() = delegate.difficultyClass
			set(value) {
				delegate = updateMongoSongChart(id, delegate.copy(difficultyClass = value))
			}
		override var difficultyValue: Int
			get() = delegate.difficultyValue
			set(value) {
				delegate = updateMongoSongChart(id, delegate.copy(difficultyValue = value))
			}
		override var d: Double?
			get() = delegate.d
			set(value) {
				delegate = updateMongoSongChart(id, delegate.copy(d = value))
			}

		override fun getRankingRecords(limit: Int, skip: Int): Collection<GameRecord> {
			return getChartRecords(id, limit, skip)
		}

		override fun getRankingPlayerRecord(userId: String): GameRecord? {
			return getPlayerBestChartRecord(id, userId)
		}

		override fun getParentSet(): SongSet {
			return getSongSetByChart(id) ?: error("chart parent not found")
		}

		override fun toString(): String = delegate.toString()
	}

	inner class GameUserDelegate(private var delegate: MongoGameUser) : GameUser {
		override val id: String
			get() = delegate.id
		override var username: String
			get() = delegate.username
			set(value) {
				require(username.isValid()) { "username contains invalid characters" }
				delegate = updateMongoGameUser(id, delegate.copy(username = value))
			}
		override var coin: Int
			get() = delegate.coin
			set(value) {
				delegate = updateMongoGameUser(id, delegate.copy(coin = value))
			}
		override var diamond: Int
			get() = delegate.diamond
			set(value) {
				delegate = updateMongoGameUser(id, delegate.copy(diamond = value))
			}
		override var ppTime: OffsetDateTime
			get() = delegate.ppTime
			set(value) {
				delegate = updateMongoGameUser(id, delegate.copy(ppTime = value))
			}
		override var isReviewer: Boolean
			get() = delegate.isReviewer
			set(value) {
				delegate = updateMongoGameUser(id, delegate.copy(isReviewer = value))
			}
		override val SongSet.isOwned: Boolean
			get() = id in delegate.ownedSongSetIds

		override fun calculateR(): Int {
			data class MiddleObject(val data: Any)
			data class AggregatedSumAcc(val sumAcc: Double)

			return collGameRec.aggregate<AggregatedSumAcc>(
				match(MongoGameRecord::playerId eq id),
				sort(descending(MongoGameRecord::playedChartId, MongoGameRecord::r, MongoGameRecord::uploadTime)),
				group(MongoGameRecord::playedChartId, Accumulators.first("data", ThisDocument)),
				Aggregates.replaceWith(MiddleObject::data),
				sort(descending(MongoGameRecord::r, MongoGameRecord::uploadTime)),
				limit(20),
				group(null, Accumulators.sum("sumAcc", MongoGameRecord::r))
			).firstOrNull()?.sumAcc?.toInt() ?: 0
		}

		override fun calculateHighestGoldenMedal(): Int {
			var highest = 0
			getAssessmentGroups()[0].assessments.sortedBy { it.medalLevel }.forEach {
				if(it.getBestAssessmentRecordForPlayer(this)?.result == 2) {
					highest = highest.coerceAtLeast(it.medalLevel)
				}
			}
			return highest
		}

		override fun changePassword(password: String) {
			require(password.isValid()) { "password contains invalid characters" }
			delegate = updateMongoGameUser(id, delegate.copy(password = password))
		}

		override fun validatePassword(password: String): Boolean {
			return password == delegate.password
		}

		override fun giveSet(setId: String) {
			delegate = updateMongoGameUser(
				id,
				delegate.copy(ownedSongSetIds = delegate.ownedSongSetIds.toMutableList().apply { add(setId) })
			)
		}

		override val ownedSets: List<SongSet>
			get() = delegate.ownedSongSetIds.mapNotNull(::getSongSetById)

		override fun calculateLastRecords(limit: Int): List<GameRecord> {
			return collGameRec.aggregate<MongoGameRecord>(
				match(MongoGameRecord::playerId eq id),
				sort(descending(MongoGameRecord::uploadTime)),
				limit(limit)
			).toList().map(::GameRecordWrap)
		}

		override fun calculateBestRecords(limit: Int, sortedBy: ScoreOrRanking): List<GameRecord> {
			data class MiddleObject(val data: Any)

			val sortingField = if(sortedBy == ScoreOrRanking.Ranking) MongoGameRecord::r else MongoGameRecord::score

			return collGameRec.aggregate<MongoGameRecord>(
				match(MongoGameRecord::playerId eq id),
				sort(descending(MongoGameRecord::playedChartId, sortingField, MongoGameRecord::uploadTime)),
				group(MongoGameRecord::playedChartId, Accumulators.first("data", ThisDocument)),
				Aggregates.replaceWith(MiddleObject::data),
				sort(descending(sortingField, MongoGameRecord::uploadTime)),
				limit(limit)
			).toList().map(::GameRecordWrap)
		}

		override fun getAllRecords(limit: Int, skip: Int): List<GameRecord> {
			return collGameRec.find().skip(skip).limit(limit).map(::GameRecordWrap).toList()
		}

		override val omegaCount: Int
			get() {
				data class Omegas(val omegas: Int)

				return collGameRec.aggregate<Omegas>(
					match(MongoGameRecord::playerId eq id),
					match(MongoGameRecord::score eq 1_000_000),
					group(MongoGameRecord::playedChartId, Accumulators.first("data", ThisDocument)),
					Aggregates.count("omegas")
				).single().omegas
			}

		override fun toString(): String = delegate.toString()
	}

	inner class GameRecordWrap(private val value: MongoGameRecord) : GameRecord {
		override val id: String
			get() = value.id
		override val playerId: String
			get() = value.playerId
		override val playedChartId: String
			get() = value.playedChartId
		override val perfect: Int
			get() = value.detail.perfect
		override val good: Int
			get() = value.detail.good
		override val miss: Int
			get() = value.detail.miss
		override val score: Int
			get() = value.score
		override val uploadTime: OffsetDateTime
			get() = value.uploadTime
		override val r: Int?
			get() = value.r
		override val ranking: Int?
			get() = null
		override val player: GameUser
			get() = getGameUserById(playerId).nn()

		override fun toString(): String = value.toString()
	}

	inner class GameRecordRankedWrap(private val value: MongoGameRecordRanked) : GameRecord {
		override val id: String
			get() = value.id
		override val playerId: String
			get() = value.playerId
		override val playedChartId: String
			get() = value.playedChartId
		override val perfect: Int
			get() = value.detail.perfect
		override val good: Int
			get() = value.detail.good
		override val miss: Int
			get() = value.detail.miss
		override val score: Int
			get() = value.score
		override val uploadTime: OffsetDateTime
			get() = value.uploadTime
		override val r: Int?
			get() = value.r
		override val ranking: Int?
			get() = value.ranking
		override val player: GameUser
			get() = getGameUserById(playerId).nn()

		override fun toString(): String = value.toString()
	}

	inner class AssessmentRecordWrap(private val value: MongoAssessRecord) : AssessmentRecord {
		override val id: String
			get() = value.id
		override val playerId: String
			get() = value.playerId
		override val assessmentId: String
			get() = value.assessmentId
		override val records: List<AssessmentRecord.AssessmentRecordEntry>
			get() = value.records.map {
				AssessmentRecord.AssessmentRecordEntry(
					it.perfect,
					it.good,
					it.miss,
					it.score,
					it.accuracy
				)
			}
		override val exRecord: AssessmentRecord.AssessmentRecordEntry?
			get() = value.exRecord?.let {
				AssessmentRecord.AssessmentRecordEntry(
					it.perfect,
					it.good,
					it.miss,
					it.score,
					it.accuracy
				)
			}
		override val uploadTime: OffsetDateTime
			get() = value.uploadTime
		override val ranking: Int?
			get() = null

		override val player: GameUser
			get() = getGameUserById(playerId).nn()
		override val result: Int
			get() {
				val ass = getAssessmentById(assessmentId).nn()
				return when {
					(sumAccuracy >= ass.goldenPassAccuracy) -> 2
					(sumAccuracy >= ass.normalPassAccuracy) -> 1
					else -> 0
				}
			}

		override fun toString(): String = value.toString()
	}

	inner class AssessmentRecordRankedWrap(private val value: MongoAssessRecordRanked) : AssessmentRecord {
		override val id: String
			get() = value.id
		override val playerId: String
			get() = value.playerId
		override val assessmentId: String
			get() = value.assessmentId
		override val records: List<AssessmentRecord.AssessmentRecordEntry>
			get() = value.records.map {
				AssessmentRecord.AssessmentRecordEntry(
					it.perfect,
					it.good,
					it.miss,
					it.score,
					it.accuracy
				)
			}
		override val exRecord: AssessmentRecord.AssessmentRecordEntry?
			get() = value.exRecord?.let {
				AssessmentRecord.AssessmentRecordEntry(
					it.perfect,
					it.good,
					it.miss,
					it.score,
					it.accuracy
				)
			}
		override val uploadTime: OffsetDateTime
			get() = value.uploadTime
		override val ranking: Int?
			get() = value.ranking

		override val player: GameUser
			get() = getGameUserById(playerId).nn()
		override val result: Int
			get() {
				val ass = getAssessmentById(assessmentId).nn()
				return when {
					(sumAccuracy >= ass.goldenPassAccuracy) -> 2
					(sumAccuracy >= ass.normalPassAccuracy) -> 1
					else -> 0
				}
			}

		override fun toString(): String = value.toString()
	}

	inner class AssessmentGroupDelegate(private var delegate: MongoAssessGroup) : AssessmentGroup {
		override val id: String
			get() = delegate.id
		override val name: String
			get() = delegate.name
		override val assessmentIds: List<String>
			get() = delegate.assessments.map { it.id }
		override val assessments: List<Assessment>
			get() = delegate.assessments.map(::AssessmentDelegate)

		override fun setAssessmentForMedal(medalLevel: Int, assessment: Assessment) {
			val replaced = delegate.assessments.toMutableList().apply {
				replaceAll {
					if(it.medalLevel == medalLevel) {
						MongoAssessment(
							assessment.id,
							medalLevel,
							assessment.healthBarLength,
							assessment.normalPassAccuracy,
							assessment.goldenPassAccuracy,
							assessment.exMissRate,
							assessment.assessmentChartIds
						)
					} else {
						it
					}
				}
			}
			delegate = updateMongoAssessmentGroup(id, delegate.copy(assessments = replaced))
		}

		override fun setAssessmentForMedal(
			medalLevel: Int,
			healthBarLength: Double,
			normalPassAccuracy: Double,
			goldenPassAccuracy: Double,
			exMissRate: Double,
			assessmentChartIds: List<String>
		) {
			val replaced = delegate.assessments.toMutableList().apply {
				replaceAll {
					if(it.medalLevel == medalLevel) {
						MongoAssessment(
							createNewRandomUUID(),
							medalLevel,
							healthBarLength,
							normalPassAccuracy,
							goldenPassAccuracy,
							exMissRate,
							assessmentChartIds
						)
					} else {
						it
					}
				}
			}
			delegate = updateMongoAssessmentGroup(id, delegate.copy(assessments = replaced))
		}

		inner class AssessmentDelegate(private var delegateAss: MongoAssessment) : Assessment {
			override val id: String
				get() = delegateAss.id
			override val medalLevel: Int
				get() = delegateAss.medalLevel
			override val healthBarLength: Double
				get() = delegateAss.healthBarLength
			override val normalPassAccuracy: Double
				get() = delegateAss.normalPassAccuracy
			override val goldenPassAccuracy: Double
				get() = delegateAss.goldenPassAccuracy
			override val exMissRate: Double
				get() = delegateAss.exMissRate
			override val assessmentChartIds: List<String>
				get() = delegateAss.assessmentChartIds
			override val assessmentCharts: List<AssessmentChart>
				get() = delegateAss.assessmentChartIds.map {
					object : AssessmentChart {
						override val id: String
							get() = it
						override val wrappingSet: SongSet
							get() = getSongSetByChart(it).nn()
					}
				}

			override fun getAssessmentRecords(limit: Int, skip: Int): List<AssessmentRecord> {
				return getAssessmentRecords(delegateAss.id, limit, skip)
			}

			override fun getBestAssessmentRecordForPlayer(user: GameUser): AssessmentRecord? {
				return getPlayerBestAssessmentRecord(this.id, user.id)
			}
		}
	}

	// endregion

	/**
	 * 直接对数据库进行操作
	 */
	@Deprecated("unused")
	fun unsafe(block: MongoDatabase.() -> Unit) {
		provider.getDatabase().block()
	}
}

@Suppress("unused")
class LabyrinthMongoBuilder(private val client: MongoClient, private val databaseName: String) {

	constructor(databaseName: String = "Explode") : this(KMongo.createClient(), databaseName)
	constructor(
		connectionString: String,
		databaseName: String = "Explode"
	) : this(KMongo.createClient(connectionString), databaseName)

	fun getClient(): MongoClient = client
	fun getDatabase(): MongoDatabase = client.getDatabase(databaseName)
}

private const val ThisDocument = "$$" + "ROOT"