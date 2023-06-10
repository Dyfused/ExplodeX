package explode2.booster.bomb

infix fun Int.clamp(closure: IntRange): Int = when {
	this < closure.first -> closure.first
	this > closure.last -> closure.last
	else -> this
}