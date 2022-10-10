package explode2.booster.graphql.proxy

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import explode2.booster.graphql.NonNegativeInt
import explode2.booster.graphql.definition.*
import explode2.simplegql.*
import explode2.simplegql.SimpleGraphQL.send
import explode2.simplegql.SingleQuery.Companion.SingleQuery
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import java.io.File

object ProxyMaze : ExplodeQuery, ExplodeMutation {

	private val logger = LoggerFactory.getLogger("ProxyMaze")

	private val linkedServer = RemoteContextCache(File(".proxy_cache"))

	private val DataFetchingEnvironment.uid: String
		get() = graphQlContext.getOrEmpty<String>("token").orElseThrow(::IllegalStateException)

	/**
	 * 将请求发送到用户对应的服务器
	 */
	private suspend inline fun <reified T> SingleQuery.sendToBind(uid: String): T {
		logger.debug("[$uid/P2R] $query - $variables")
		return send<T>(
			linkedServer[uid]!!.remoteServer, linkedServer[uid]!!.remoteSoudayo
		).apply { logger.debug("[$uid/R2P] $this") }
	}

	override suspend fun loginUser(env: DataFetchingEnvironment, username: String?, password: String?): UserModel {
		require(!username.isNullOrBlank())
		require(!password.isNullOrBlank())

		val auth = password.split(":", limit = 3)
		val uid = auth[0] // unique id for this server
		val usr = auth[1]
		val pwd = auth[2]

		linkedServer[uid] = RemoteContext(username, null)

		val rsp = SingleQuery(
			"""
			mutation login(!un: String, !pw: String) {
			    r: loginUser(username: !un, password: !pw) {
			        _id
			        username
			        token
			        coin
			        diamond
			        PPTime
			        RThisMonth
			        access { reviewer }
			    }
			}
		""".trimIndent()
		).variable("un", usr).variable("pw", pwd).send<GraphQLDataR<UserModel>>(linkedServer[uid]!!.remoteServer)

		val token = rsp.data.r.token
		linkedServer[uid]!!.remoteSoudayo = token
		linkedServer.save() // 储存 token

		logger.info("Configured proxy user $uid to remote addr $username with token $token")

		// 劫持 token
		return rsp.data.r.copy(token = uid)
	}

	override suspend fun registerUser(env: DataFetchingEnvironment, username: String?, password: String?): UserModel {
		// 不支持注册
		throw UnsupportedOperationException("Register is not supported")
	}

	override suspend fun exchangeSet(env: DataFetchingEnvironment, setId: String?): ExchangeModel {
		requireNotNull(setId)

		return SingleQuery(
			"""
			mutation exchange(!id: String) {
			    r:exchangeSet(setId: !id) {
			        coin
			    }
			}
		""".trimIndent()
		).variable("id", setId).sendToBind<GraphQLDataR<ExchangeModel>>(env.uid).data.r
	}

	override suspend fun submitBeforeAssessment(
		env: DataFetchingEnvironment, assessmentGroupId: String?, medalLevel: Int?
	): BeforePlaySubmitModel {
		requireNotNull(assessmentGroupId)
		requireNotNull(medalLevel)

		return SingleQuery(
			"""
			mutation ba(!gid:String,!md:Int) {
			    r:submitBeforeAssessment(assessmentGroupId:!gid,medalLevel:!md) {
			        PPTime
			        playingRecord {
			            randomId
			        }
			    }
			}
			""".trimIndent()
		).variable("gid", assessmentGroupId).variable("md", medalLevel.toString())
			.sendToBind<GraphQLDataR<BeforePlaySubmitModel>>(env.uid).data.r
	}

	override suspend fun submitAfterAssessment(
		env: DataFetchingEnvironment, playRecords: List<PlayRecordInput?>?, randomId: String?
	): AfterAssessmentModel {
		requireNotNull(playRecords)
		requireNotNull(randomId)

		return SingleQuery(
			"""
					mutation sa(!rec:[PlayRecordInput],!rid:String) {
					    r:submitAfterAssessment(playRecords:!rec,randomId:!rid) {
					        coin
					        diamond
					        RThisMonth
					        result
					    }
					}
				""".trimIndent()
		).variable("rec", jacksonObjectMapper().writeValueAsString(playRecords)).variable("rid", randomId)
			.sendToBind<GraphQLDataR<AfterAssessmentModel>>(env.uid).data.r
	}

	override suspend fun submitBeforePlay(
		env: DataFetchingEnvironment, chartId: String?, PPCost: Int?, eventArgs: String?
	): BeforePlaySubmitModel {
		requireNotNull(chartId)
		requireNotNull(PPCost)

		return SingleQuery(
			"""
			mutation bp(!cid:String,!pp:Int) {
			    r:submitBeforePlay(chartId:!cid, PPCost:!pp, eventArgs:"") {
			        PPTime
			        playingRecord { randomId }
			    }
			}
		""".trimIndent()
		).variable("cid", chartId).variable("pp", PPCost.toString())
			.sendToBind<GraphQLDataR<BeforePlaySubmitModel>>(env.uid).data.r
	}

	override suspend fun submitAfterPlay(
		env: DataFetchingEnvironment, randomId: String?, playRecord: PlayRecordInput?
	): AfterPlaySubmitModel {
		requireNotNull(randomId)
		requireNotNull(playRecord)
		requireNotNull(playRecord.score)
		requireNotNull(playRecord.perfect)
		requireNotNull(playRecord.good)
		requireNotNull(playRecord.miss)

		return SingleQuery(
			"""
			mutation ap(!bleed:Boolean !alive:Boolean !mirror:Boolean !s:Int !p:Int !g:Int !m:Int !rd: String) {
			    r:submitAfterPlay(randomId: !rd, playRecord:{mod:{narrow:1.0,speed:1.0,isBleed:!bleed,isMirror:!mirror} isAlive:!alive score:!s perfect:!p good:!g miss:!m}) {
			        coin
			        RThisMonth
			        diamond
			        ranking {
			            isPlayRankUpdated
			            playRank{ rank }
			        }
			    }
			}
		""".trimIndent()
		).variable("bleed", "true").variable("alive", "true").variable("mirror", "false")
			.variable("s", playRecord.score.toString()).variable("p", playRecord.perfect.toString())
			.variable("g", playRecord.good.toString()).variable("m", playRecord.miss.toString())
			.variable("rd", randomId).sendToBind<GraphQLDataR<AfterPlaySubmitModel>>(env.uid).data.r
	}

	override suspend fun hello(env: DataFetchingEnvironment): String {
		val uid = env.uid
		val ctx = linkedServer[uid]

		val remoteHello = SingleQuery("query { hello }").sendToBind<GraphQLData<ObjectNode>>(uid)

		return "User[$uid] combined with remote ${ctx?.remoteServer}, logged in as ${ctx?.remoteSoudayo}, remote response ${remoteHello.data}"
	}

	override suspend fun gameSetting(): GameSettingModel {
		// 使用默认值
		return GameSettingModel(81)
	}

	override suspend fun reviewer(env: DataFetchingEnvironment): Reviewer {
		return ProxyMazeReviewer
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
		requireNotNull(playCountOrder)
		requireNotNull(publishTimeOrder)
		requireNotNull(limit)
		requireNotNull(skip)
		requireNotNull(isHidden)
		requireNotNull(musicTitle)
		requireNotNull(isOfficial)
		requireNotNull(isRanked)

		return SingleQuery(
			"""
			query fetchSets(!lim:NonNegativeInt !skip:NonNegativeInt !searchTitle:String !orderPlay:Int !orderTime:Int
			    !hidden:Int !official:Int !ranked:Int) {
			    r:set(playCountOrder:!orderPlay publishTimeOrder:!orderTime limit:!lim skip:!skip
			        isHidden:!hidden musicTitle:!searchTitle isOfficial:!official isRanked:!ranked) {
			        _id
			        introduction
			        coinPrice
			        isGot
			        isRanked
			        noter { username }
			        musicTitle
			        composerName
			        playCount
			        chart{ _id difficultyClass difficultyValue }
			    }
			}
		""".trimIndent()
		).variable("lim", limit.value.toString()).variable("skip", skip.value.toString())
			.variable("searchTitle", musicTitle).variable("orderPlay", playCountOrder.toString())
			.variable("orderTime", publishTimeOrder.toString()).variable("hidden", isHidden.toString())
			.variable("official", isOfficial.toString()).variable("ranked", isRanked.toString())
			.sendToBind<GraphQLDataR<List<SetModel>>>(env.uid).data.r
	}

	override suspend fun self(env: DataFetchingEnvironment): Self {
		return ProxyMazeSelf
	}

	override suspend fun assessmentGroup(
		env: DataFetchingEnvironment, limit: Int?, skip: Int?
	): List<AssessmentGroupModel> {
		requireNotNull(limit)
		requireNotNull(skip)

		return SingleQuery(
			"""
			query asg{
			    r:assessmentGroup(limit:1,skip:0) {
			        _id
			        name
			        assessment {
			            _id
			            medalLevel
			            lifeBarLength
			            normalPassAcc
			            goldenPassAcc
			            assessmentRecord {
			                isBest
			                achievementRate
			                playRecord { perfect good miss score}
			            }
			            exMiss
			            chart {
			                _id
			                set {
			                    _id
			                    introduction
			                    coinPrice
			                    noter { username }
			                    musicTitle
			                    composerName
			                    chart{
			                        difficultyClass
			                        difficultyValue
			                        _id
			                    }
			                    isGot
			                    isRanked
			                }
			            }
			        }
			    }
			}
				""".trimIndent()
		).sendToBind<GraphQLDataR<List<AssessmentGroupModel>>>(env.uid).data.r
	}

	override suspend fun assessmentRank(
		env: DataFetchingEnvironment,
		assessmentGroupId: String?,
		medalLevel: Int?,
		limit: NonNegativeInt?,
		skip: NonNegativeInt?
	): List<AssessmentRecordWithRankModel> {
		requireNotNull(assessmentGroupId)
		requireNotNull(medalLevel)
		requireNotNull(limit)
		requireNotNull(skip)

		return SingleQuery(
			"""
			query (!assessmentGroupId: String, !medalLevel: Int, !skip: NonNegativeInt, !limit: NonNegativeInt) {
			    r:assessmentRank(assessmentGroupId: !assessmentGroupId, medalLevel: !medalLevel, skip: !skip, limit: !limit) {
			        createTime
			        rank
			        achievementRate
			        result
			        player {
			            _id
			            username
			            highestGoldenMedalLevel
			            RThisMonth
			        }
			    }
			}
			""".trimIndent()
		).variable("assessmentGroupId", assessmentGroupId).variable("medalLevel", medalLevel.toString())
			.variable("skip", skip.value.toString()).variable("limit", limit.value.toString())
			.sendToBind<GraphQLDataR<List<AssessmentRecordWithRankModel>>>(env.uid).data.r
	}

	override suspend fun setById(env: DataFetchingEnvironment, _id: String?): SetModel {
		requireNotNull(_id)

		return SingleQuery(
			"""
			query singleSet(!id:String) {
			    r:setById(_id:!id) {
			        _id
			        introduction
			        coinPrice
			        noter{ username }
			        musicTitle
			        composerName
			        chart { difficultyClass difficultyValue _id }
			        isGot
			        isRanked
			    }
			}
		""".trimIndent()
		).variable("id", _id.toString()).sendToBind<GraphQLDataR<SetModel>>(env.uid).data.r
	}

	override suspend fun userByUsername(env: DataFetchingEnvironment, username: String?): UserModel? {
		requireNotNull(username)

		return SingleQuery(
			"""
			query fetchUser(!un:String){
			    r: userByUsername(username:!un) {
			        username
			        _id
			        RThisMonth
			    }
			}
			""".trimIndent()
		).variable("un", username).sendToBind<GraphQLDataR<UserModel?>>(env.uid).data.r
	}

	override suspend fun playRank(
		env: DataFetchingEnvironment, chartId: String?, skip: NonNegativeInt?, limit: NonNegativeInt?
	): List<PlayRecordWithRankModel> {
		requireNotNull(chartId)
		requireNotNull(skip)
		requireNotNull(limit)

		return SingleQuery(
			"""
			query (!chartId: String, !skip: NonNegativeInt, !limit: NonNegativeInt) {
			    r: playRank(chartId: !chartId, skip: !skip, limit: !limit) {
			        createTime
			        rank
			        score
			        perfect
			        good
			        miss
			        mod {
			            narrow
			            speed
			            isBleed
			            isMirror
			        }
			        player {
			            _id
			            username
			            highestGoldenMedalLevel
			            RThisMonth
			        }
			    }
			}
			""".trimIndent()
		).variable("chartId", chartId).variable("skip", skip.value.toString()).variable("limit", limit.value.toString())
			.sendToBind<GraphQLDataR<List<PlayRecordWithRankModel>>>(env.uid).data.r
	}

	override suspend fun refreshSet(
		env: DataFetchingEnvironment, setVersion: List<ChartSetAndVersionInputModel>
	): List<RefreshSetModel> {
		return SingleQuery(
			"""
			query refresh {
			    refreshSet(setVersion: [{setId: !id, version: -1}]) {
			        isRanked
    				introduction
    				noterName
    				musicTitle
    				_id
			    }
			}
		""".trimIndent()
		).variable("setId", jacksonObjectMapper().writeValueAsString(setVersion))
			.sendToBind<GraphQLDataR<List<RefreshSetModel>>>(env.uid).data.r
	}

	object ProxyMazeSelf : Self {
		override suspend fun gotSet(env: DataFetchingEnvironment): List<SetModel> {
			return SingleQuery(
				"""
				query {
				    r:self {
				        gotSet {
				            _id
				            introduction
				            coinPrice
				            isGot
				            isRanked
				            noter { username }
				            musicTitle
				            composerName
				            playCount
				            chart{ _id difficultyClass difficultyValue }
				        }
				    }
				}
				""".trimIndent()
			).sendToBind<GraphQLDataR<List<SetModel>>>(env.uid).data.r
		}

		override suspend fun assessmentRankSelf(
			env: DataFetchingEnvironment, assessmentGroupId: String?, medalLevel: Int?
		): AssessmentRecordWithRankModel? {
			requireNotNull(assessmentGroupId)
			requireNotNull(medalLevel)

			return SingleQuery(
				"""
				query (!assessmentGroupId: String, !medalLevel: Int) {
				    r:self {
				        r:assessmentRankSelf(assessmentGroupId: !assessmentGroupId, medalLevel: !medalLevel) {
				            createTime
				            rank
				            achievementRate
				            result
				            player {
				                _id
				                username
				                highestGoldenMedalLevel
				                RThisMonth
				            }
				        }
				    }
				}
				""".trimIndent()
			).variable("assessmentGroupId", assessmentGroupId).variable("medalLevel", medalLevel.toString())
				.sendToBind<GraphQLDataR<AssessmentRecordWithRankModel?>>(env.uid).data.r
		}

		override suspend fun playRankSelf(env: DataFetchingEnvironment, chartId: String?): PlayRecordWithRankModel? {
			requireNotNull(chartId)

			return SingleQuery(
				"""
				query (!chartId: String) {
			        r: self {
			            r: playRankSelf(chartId: !chartId) {
			                createTime
			                rank
			                score
			                perfect
			                good
			                miss
			                mod {
			                    narrow
			                    speed
			                    isBleed
			                    isMirror
			                }
			                player {
			                    _id
			                    username
			                    highestGoldenMedalLevel
			                    RThisMonth
			                }
			            }
			        }
			    }
				""".trimIndent()
			).variable("chartId", chartId).sendToBind<GraphQLDataR<PlayRecordWithRankModel?>>(env.uid).data.r
		}
	}

	object ProxyMazeReviewer : Reviewer {
		override suspend fun reviewRequest(
			env: DataFetchingEnvironment, limit: Int?, skip: Int?, status: Int?, searchStr: String?
		): List<ReviewRequest> {
			requireNotNull(limit)
			requireNotNull(skip)
			requireNotNull(status)
			requireNotNull(searchStr)

			return SingleQuery(
				"""
				query reviewSets(!lim:Int,!skip:Int,!search:String) {
				    r:reviewer {
				        reviewRequest(limit:!lim,skip:!skip,status:1,searchStr:!search) {
				            isUnranked
				            set {
				                _id
				                introduction
				                coinPrice
				                isGot
				                noter { username }
				                musicTitle
				                composerName
				                chart{ _id difficultyClass difficultyValue }
				            }
				        }
				    }
				}
				""".trimIndent()
			).variable("lim", limit.toString()).variable("skip", skip.toString())
				.variable("search", searchStr.toString()).sendToBind<GraphQLDataR<List<ReviewRequest>>>(env.uid).data.r
		}
	}
}