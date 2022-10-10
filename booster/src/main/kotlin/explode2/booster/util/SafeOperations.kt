package explode2.booster.util

import java.util.stream.Stream

fun <T> Iterable<T>.forEachExceptional(block: (T) -> Unit, exceptional: (T, exception: Throwable) -> Unit) {
	this.forEach { el ->
		el.runCatching(block).onFailure { exceptional(el, it) }
	}
}

fun <T, R> Iterable<T>.mapExceptional(block: (T) -> R, exceptional: (T, exception: Throwable) -> Unit): List<R> {
	val wrappedBlock = { el: T -> el.runCatching(block).onFailure { exceptional(el, it) }.getOrNull() }
	return this.mapNotNull(wrappedBlock)
}