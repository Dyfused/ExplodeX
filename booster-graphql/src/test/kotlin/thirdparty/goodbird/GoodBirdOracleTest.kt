package thirdparty.goodbird

import org.junit.jupiter.api.Test

internal class GoodBirdOracleTest {

	private val perf = 1761
	private val good = 59
	private val miss = 40
	private val dValue = 15.2

	@Test
	fun calculateAccuracy() {
		val acc = //0.9626344086021505
			GoodBirdOracle.calculateAccuracy(perf, good, miss)

		require(acc == 0.9626344086021505)
	}

	@Test
	fun calculateRank() {
		val acc = //0.9626344086021505
			GoodBirdOracle.calculateAccuracy(perf, good, miss)

		val rank = GoodBirdOracle.calculateRank(acc, 15.2)
		require(rank == 551)
	}

	@Test
	fun calculateCoin() {
		val acc = //0.9626344086021505
			GoodBirdOracle.calculateAccuracy(perf, good, miss)

		val coin = GoodBirdOracle.calculateCoin(acc, dValue)
		require(coin == 182)
	}
}