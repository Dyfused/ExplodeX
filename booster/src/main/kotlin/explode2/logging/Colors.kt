package explode2.logging

typealias Color = String

@Suppress("unused", "SpellCheckingInspection")
object Colors {
	val Miku = 0x66FFCC.toColor()
	val TianYi = 0x66CCFF.toColor()
	val TaskPurple = 0xFF66CC.toColor()
}

private val ColorChar = Char(167)
private fun Int.toColor() = "${ColorChar}x"+Integer.toHexString(this).map { "$ColorChar$it" }.joinToString(separator = "")
private fun Char.toColor() = "${ColorChar}$this"