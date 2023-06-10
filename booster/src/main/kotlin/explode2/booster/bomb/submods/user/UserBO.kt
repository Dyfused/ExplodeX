package explode2.booster.bomb.submods.user

import explode2.gateau.GameUser
import java.time.format.DateTimeFormatter

internal data class UserBO(
	val id: String,
	val username: String,
	val coin: Int,
	val diamond: Int,
	val pptime: String,
	val reviewer: Boolean,
	val bought_sets: List<String>,
	val r: Int,
	val highest_golden_medal: Int,
	val omega_count: Int
)

@Suppress("DEPRECATION")
internal fun GameUser.toBO() =
	UserBO(
		id, username, coin, diamond,
		ppTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), isReviewer,
		ownedSets.map { it.id }, calculateR(), calculateHighestGoldenMedal(),
		omegaCount
	)