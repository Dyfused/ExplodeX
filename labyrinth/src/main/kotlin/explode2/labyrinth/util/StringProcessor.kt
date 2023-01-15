package explode2.labyrinth.util

class StringProcessor(private val rawString: String, private var index: Int = 0) {

	private val readable get() = rawString.length > index

	val consumed get() = rawString.substring(0, index)
	val remaining get() = if(readable) rawString.substring(index) else ""

	fun consume(str: String): Boolean {
		if(remaining.startsWith(str)) {
			index += str.length
			return true
		}
		return false
	}

	val remainingAsInt: Int? get() = remaining.toIntOrNull()
	val remainingAsDouble: Double? get() = remaining.toDoubleOrNull()
	val remainingAsBoolean: Boolean? get() = remaining.toBooleanStrictOrNull()

}