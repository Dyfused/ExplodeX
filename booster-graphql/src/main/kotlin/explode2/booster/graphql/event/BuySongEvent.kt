package explode2.booster.graphql.event

import explode2.gateau.GameUser
import explode2.gateau.SongSet

/**
 * 即将购买曲目时触发。
 *
 * 使用 [reject] 拒绝购买，使用 [accept] 允许购买。
 */
data class BuySongEvent(
	val user: GameUser,
	val song: SongSet
) {

	private var value: Pair<Status, String?> = Status.ACCEPT to null

	fun accept() {
		value = Status.ACCEPT to null
	}

	fun reject(reason: String? = null) {
		value = Status.REJECT to reason
	}

	val status = value.first
	val message = value.second ?: "Rejected"

	enum class Status {
		ACCEPT, REJECT
	}

}
