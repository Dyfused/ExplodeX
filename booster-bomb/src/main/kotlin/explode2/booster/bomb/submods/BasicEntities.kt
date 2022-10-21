package explode2.booster.bomb.submods

data class Data<T>(
	val success: Boolean,
	val message: String?,
	val data: T
)

fun <T> T.toData(message: String? = null, success: Boolean = true) =
	Data(success, message, this)

data class Error(
	val success: Boolean,
	val message: String?,
	val stackTrace: String?
)

/**
 * 根据 [Throwable] 生成 [Error] 实例
 */
fun Throwable.toError(message: String? = null, success: Boolean = false) =
	Error(success, message ?: this.message, stackTraceToString())

fun toError(message: String, success: Boolean = false) =
	Error(success, message, Exception().stackTraceToString())