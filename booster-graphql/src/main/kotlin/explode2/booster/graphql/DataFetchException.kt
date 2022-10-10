package explode2.booster.graphql

import org.jetbrains.annotations.Contract

class DataFetchException(message: String?, cause: Throwable?) : Exception(message, cause)

fun boom(message: String? = null, cause: Throwable? = null): Nothing = throw DataFetchException(message, cause)

@Contract("!null -> param1; null -> fail")
fun <T> T?.baah(errorMessage: String = "[Baah] Required value is null") = this ?: boom(errorMessage)
// fun <T> T.baah(errorMessage: () -> String) = this ?: boom(errorMessage())