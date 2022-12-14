package explode2.booster.util

fun <T> Iterable<T>.forEachExceptional(block: (T) -> Unit, exceptional: (T, exception: Throwable) -> Unit) {
	this.forEach { el ->
		el.runCatching(block).onFailure { exceptional(el, it) }
	}
}

