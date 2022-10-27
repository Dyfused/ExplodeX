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

data class ExceptionContext(val message: String, val exception: Throwable)

/**
 * 根据 [Throwable] 生成 [Error] 实例
 */
fun Throwable.toError(message: String? = null, success: Boolean = false) =
	Error(success, message ?: this.message, stackTraceToString())

fun toError(message: String, success: Boolean = false, needStackTrace: Boolean = false) =
	Error(success, message, if(needStackTrace) Exception().stackTraceToString() else null)

fun toError(operationName: String, success: Boolean = false, contexts: List<ExceptionContext>) =
	Error(success, buildString {
		// single   => Exception occurred when executing {OperationName}:
		// multiple => Exception occurred multiple times when executing {OperationName}:
		append("Exception occurred ")
		if(contexts.size != 1) append("multiple times ")
		append("when executing ")
		append(operationName)
		append(":")
		appendLine()
		// for each exception => [{SimpleClassName}] {Message of Context} - {Message of Exception}
		contexts.forEach {
			append("[")
			append(it.exception.javaClass.simpleName)
			append("] ")
			append(it.message)
			append(" - ")
			append(it.exception.message)
			appendLine()
		}
	}, null)