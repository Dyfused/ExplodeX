package explode2.tests

import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Field
import explode2.labyrinth.SearchCategory
import explode2.labyrinth.SearchSort
import explode2.labyrinth.mongo.po.MongoGameRecord
import explode2.labyrinth.mongo.po.MongoSongChart
import explode2.labyrinth.mongo.po.MongoSongSet
import explode2.labyrinth.util.StringProcessor
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoUtil
import java.util.regex.PatternSyntaxException

private val ThisDocument = "$$" + "ROOT"

fun main() {
	for(i in 0..2) {
		main(i * 9, 9)
	}
}

fun main(skip: Int = 0, limit: Int = 9) {

	val collSet = KMongo.createClient().getDatabase("explodeCompetition").getCollection("Sets")

	val matchingName = ""
	val matchingCategory = SearchCategory.ALL
	val sortBy = SearchSort.DESCENDING_BY_PUBLISH_TIME

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
			pipeline += sort(descending(MongoSongSet::publishTime, MongoSongSet::musicName, MongoSongSet::id))
		}

		// FIXME: 修复性能问题
		SearchSort.DESCENDING_BY_PLAY_COUNT -> {
			// 添加游玩次数查询
			pipeline += lookup("GameRecords", "chartIds", "playedChartId", "playRecords")
			pipeline += addFields(
				Field(
				"playCount",
				MongoOperator.size.from("\$playRecords")
			)
			)
			pipeline += sort(descending(SongSetWithPlayCount::playCount, MongoSongSet::musicName, MongoSongSet::id))
		}
	}

	// 添加限制
	pipeline += skip(skip)
	pipeline += limit(limit)

	collSet.aggregate<MongoSongSet>(*pipeline.toTypedArray()).toList().forEach(::println)
}