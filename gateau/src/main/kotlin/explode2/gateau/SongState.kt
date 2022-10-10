package explode2.gateau

interface SongState {

	var category: Int

	var isHidden: Boolean

	var isReviewing: Boolean

	val isUnRanked get() = category == UnRanked
	val isRanked get() = category == Ranked || category == Official
	val isOfficial get() = category == Official

	fun unranked() = apply { category = UnRanked }
	fun ranked() = apply { category = Ranked }
	fun official() = apply { category = Official }

	companion object {
		const val UnRanked = 0
		const val Ranked = 1
		const val Official = 2

		fun ofRanked() = BasicSongState(Ranked)
		fun ofUnRanked() = BasicSongState(UnRanked)
		fun ofOfficial() = BasicSongState(Official)
	}

	class BasicSongState(
		override var category: Int,
		override var isHidden: Boolean = false,
		override var isReviewing: Boolean = false
	) : SongState
}