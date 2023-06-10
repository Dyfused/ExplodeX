package graphql.event

/**
 * 用户登录前触发。
 *
 * 使用 [reject] 拒绝登录。
 */
data class UserLoginEvent(
	val username: String,
	var password: String
) {

	private var reject: Boolean = false
	private var rejectReason: String = "Login failed"

	fun reject(reason: String? = null) {
		reject = true
		if(reason != null) {
			rejectReason = reason
		}
	}

	fun isRejected() = reject
	fun getRejectReason() = rejectReason

}
