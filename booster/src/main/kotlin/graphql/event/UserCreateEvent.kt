package graphql.event

/**
 * 即将注册用户时触发。
 *
 * 使用 [reject] 取消。
 */
data class UserCreateEvent(
	val username: String,
	var password: String
) {
	private var reject: Boolean = false
	private var rejectReason: String = "Your application is rejected"

	fun reject(reason: String? = null) {
		reject = true
		if(reason != null) {
			rejectReason = reason
		}
	}

	fun isRejected() = reject
	fun getRejectReason() = rejectReason
}
