package explode2.booster.graphql

@JvmInline
value class NonNegativeInt(val value: Int) {
	companion object {
		val NonNegativeInt?.int get() = this?.value
	}
}
