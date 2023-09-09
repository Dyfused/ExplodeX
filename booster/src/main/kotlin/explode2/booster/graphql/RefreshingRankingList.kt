package explode2.booster.graphql

import explode2.booster.graphql.definition.PlayRecordWithRankModel
import explode2.gateau.GameRecord
import explode2.labyrinth.GameRecordRepository
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

data class RefreshingRankingList(
	val chartId: String,
	val expireHours: Int,
	val repo: GameRecordRepository
) {

	private var cache: List<PlayRecordWithRankModel>? = null
	private var lastUpdateTime: LocalDateTime? = null

	fun clear() {
		cache = null
		lastUpdateTime = null
	}

	private fun update() {
		cache = repo.getChartRecords(chartId, 100, 0).map(GameRecord::tunerize)
		lastUpdateTime = LocalDateTime.now()
	}

	fun get(): List<PlayRecordWithRankModel> {
		val lastUpdateTime = lastUpdateTime
		if(
			lastUpdateTime == null ||
			lastUpdateTime + 24.hours.toJavaDuration() <= LocalDateTime.now() ||
			cache == null
		) {
			update()
		}

		return cache!!
	}

	fun get(limit: Int, skip: Int): List<PlayRecordWithRankModel> {
		require(limit > 0)
		require(skip >= 0)
		return get().drop(skip).take(limit)
	}

	fun get(playerId: String): PlayRecordWithRankModel? {
		val lastUpdateTime = lastUpdateTime
		if(
			lastUpdateTime == null ||
			lastUpdateTime + 24.hours.toJavaDuration() <= LocalDateTime.now() ||
			cache == null
		) {
			update()
		}

		return cache!!.firstOrNull { it.player._id == playerId }
	}

	companion object {

		private val instances = mutableMapOf<String, RefreshingRankingList>()

		fun getOrCreate(chartId: String, expireHours: Int, repo: GameRecordRepository): RefreshingRankingList {
			return instances.getOrPut(chartId) { RefreshingRankingList(chartId, expireHours, repo) }
		}
	}

}