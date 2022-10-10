package explode2.gateau

import java.time.OffsetDateTime

interface SongSet {

	val id: String

	var musicName: String
	var musicComposer: String

	var introduction: String

	var coinPrice: Int

	var noterName: String
	var noterUserId: String?

	var chartIds: List<String>
	val charts: List<SongChart>

	val playCount: Int
	val publishTime: OffsetDateTime

	val state: SongState
	fun isUserGot(user: GameUser): Boolean

}