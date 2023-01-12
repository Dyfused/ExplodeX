package explode2.logging

typealias Color = String

@Suppress("unused", "SpellCheckingInspection")
object Colors {
	val Miku = 0x66FFCC.toColor()
	val TianYi = 0x66CCFF.toColor()
	val TaskPurple = 0xFF66CC.toColor()

	object Light {
		val Red = 0xF2705E.toColor()
		val Purple = 0xB55EF7.toColor()
		val Blue = 0x60B0E0.toColor()
		val Green = 0x5EF777.toColor()
		val Yellow = 0xEDDF5A.toColor()
	}
}

private val ColorChar = Char(167)
private fun Int.toColor() = "${ColorChar}x"+Integer.toHexString(this).map { "$ColorChar$it" }.joinToString(separator = "")
private fun Char.toColor() = "${ColorChar}$this"