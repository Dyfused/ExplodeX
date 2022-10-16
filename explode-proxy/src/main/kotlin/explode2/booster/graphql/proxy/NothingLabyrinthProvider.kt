package explode2.booster.graphql.proxy

import explode2.gateau.*
import explode2.labyrinth.*
import java.time.OffsetDateTime

/**
 * 用于代理服务器的 LabyrinthProvider，
 * 因为代理服务器不需要数据库，所以这里的所有内容提供 API 都不需要实现。
 */
class NothingLabyrinthProvider : LabyrinthProvider {

	override val gameUserFactory: GameUserFactory
		get() = getAndError()
	override val songSetFactory: SongSetFactory
		get() = object : SongSetFactory {
			override fun getSongSetById(id: String): SongSet? {
				getAndError()
			}

			override fun getSongSetByMusic(musicName: String): SongSet? {
				getAndError()
			}

			override fun getSongSetByChart(chartId: String): SongSet {
				return object : SongSet {
					override val id: String
						get() = ""
					override var musicName: String
						get() = getAndError()
						set(_) {}
					override var musicComposer: String
						get() = getAndError()
						set(_) {}
					override var introduction: String
						get() = getAndError()
						set(_) {}
					override var coinPrice: Int
						get() = getAndError()
						set(_) {}
					override var noterName: String
						get() = getAndError()
						set(_) {}
					override var noterUserId: String?
						get() = getAndError()
						set(_) {}
					override var chartIds: List<String>
						get() = getAndError()
						set(_) {}
					override val charts: List<SongChart>
						get() = getAndError()
					override val playCount: Int
						get() = getAndError()
					override val publishTime: OffsetDateTime
						get() = getAndError()
					override val state: SongState
						get() = getAndError()

					override fun isUserGot(user: GameUser): Boolean {
						getAndError()
					}
				}
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
				getAndError()
			}

			override fun searchSongSets(
				matchingName: String?,
				matchingCategory: SearchCategory?,
				sortBy: SearchSort?,
				limit: Int,
				skip: Int
			): Collection<SongSet> {
				getAndError()
			}
		}
	override val songChartFactory: SongChartFactory
		get() = getAndError()
	override val assessmentInfoFactory: AssessmentInfoFactory
		get() = getAndError()
	override val gameRecordFactory: GameRecordFactory
		get() = getAndError()
	override val assessmentRecordFactory: AssessmentRecordFactory
		get() = getAndError()

	private fun getAndError(): Nothing = error("NothingLabyrinthProvider provides nothing, plugins should never try getting the factories!")
}