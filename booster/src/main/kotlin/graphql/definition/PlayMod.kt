package graphql.definition

data class PlayMod(
	val narrow: Int = 0,
	val speed: Int = 0,
	val isBleed: Boolean = false,
	val isMirror: Boolean = false
) {
	companion object {
		val Default = PlayMod()
	}
}
