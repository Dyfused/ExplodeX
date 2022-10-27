package explode2.booster.bomb.submods.chart

import explode2.gateau.SongSet
import java.time.format.DateTimeFormatter

internal data class SetBO(
	val id: String,
	val music_name: String,
	val music_composer: String,
	val introduction: String,
	val coin_price: Int,
	val noter_name: String,
	val noter_user_id: String?,
	val child_charts: List<String>,
	val play_count: Int,
	val publish_time: String,
	val category: Int,
	val hidden: Boolean,
	val reviewing: Boolean,
)

internal fun SongSet.toBO() = SetBO(
	id,
	musicName, musicComposer,
	introduction,
	coinPrice,
	noterName, noterUserId,
	chartIds,
	playCount,
	publishTime.format(DateTimeFormatter.ISO_DATE_TIME),
	state.category,
	state.isHidden,
	state.isReviewing
)