package explode2.booster.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private val ENABLED = System.getenv("DEV")?.isNotEmpty() == true

@OptIn(ExperimentalTime::class)
fun <T> measureTimedValueAndPrint(
	subject: String = "",
	template: (subject: String, timeUsed: Duration, avgTime: Duration) -> String = { s, t, a -> "[TimeMeasure/$s] $t (avg: $a)" },
	block: () -> T
): T {
	return if(ENABLED) {
		measureTimedValue(block).let {
			println(template(subject, it.duration, getAvgDuration(subject)))
			putAvgDuration(subject, it.duration)
			it.value
		}
	} else {
		block()
	}
}

private val subjectDurations = mutableMapOf<String, MutableList<Duration>>()

private fun putAvgDuration(subject: String, duration: Duration) {
	subjectDurations.getOrPut(subject, ::mutableListOf).add(duration)
}

private fun getAvgDuration(subject: String): Duration {
	val durations = subjectDurations[subject] ?: return 0.seconds
	val avgMs = durations.map { it.inWholeNanoseconds }.average()
	return avgMs.nanoseconds
}