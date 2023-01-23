package explode2.labyrinth.mongo

import java.util.*

internal fun <T> T?.nn(propertyName: String): T = this ?: error("$propertyName is null")

private val ValidChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
private val ValidLowercaseChars = ('a'..'z') + ('0'..'9')

internal fun createNewRandomChartId() = List(24) { ValidChars.random() }.joinToString(separator = "")
internal fun createNewRandomSetId() = List(24) { ValidLowercaseChars.random() }.joinToString(separator = "")
internal fun createNewRandomUUID() = UUID.randomUUID().toString()