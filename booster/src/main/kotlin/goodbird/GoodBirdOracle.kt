package goodbird

import kotlin.math.pow

/**
 * 来自某位 `一般路过鸟` 的神谕。
 *
 * 我们只需要铭记这位好鸟，不必知晓他的真实身份。如果你知道，也请你保密。
 */
object GoodBirdOracle {

	fun calculateAccuracy(perfect: Int, good: Int, miss: Int): Double {
		return (perfect + (good / 2.0)) / (perfect + good + miss)
	}

	fun calculateRank(accuracy: Double, dValue: Double): Int {
		return maxOf(accuracy * 50.0, accuracy.pow(8) * dValue.pow(3) - dValue.pow(2.8)).toInt()
	}

	fun calculateCoin(accuracy: Double, dValue: Double): Int {
		return (1.1.pow(dValue) * accuracy.pow(4) * 50).toInt()
	}

}