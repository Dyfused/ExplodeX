@file:Suppress("DEPRECATION", "DeprecatedCallableAddReplaceWith")

package explode2.logging

import org.slf4j.LoggerFactory

private val debugLogger = LoggerFactory.getLogger("DEBUG!")

@Deprecated("生成环境不要用")
fun logDebug(message: String) =
	debugLogger.warn(message)

@Deprecated("生成环境不要用")
fun <T: Any> T.logDebug() =
	logDebug(this.toString())