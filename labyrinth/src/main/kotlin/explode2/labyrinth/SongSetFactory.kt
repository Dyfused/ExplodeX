package explode2.labyrinth

import explode2.gateau.*
import java.time.OffsetDateTime

interface SongSetFactory {

	fun getSongSetById(id: String): SongSet?

	fun getSongSetByMusic(musicName: String): SongSet?

	fun getSongSetByChart(chartId: String): SongSet?

	fun createSongSet(
		musicName: String,
		musicComposer: String,
		introduction: String,
		noterName: String,
		charts: List<String>,
		state: SongState,
		id: String? = null,
		coinPrice: Int = 0,
		publishTime: OffsetDateTime = OffsetDateTime.now(),
		noterUser: GameUser? = null
	): SongSet

	fun searchSongSets(matchingName: String?, matchingCategory: SearchCategory?, sortBy: SearchSort?, limit: Int = 0, skip: Int = 0): Collection<SongSet>

}