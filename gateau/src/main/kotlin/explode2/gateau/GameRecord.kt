package explode2.gateau

import java.time.OffsetDateTime

interface GameRecord {

	val id: String

	val playerId: String
	val playedChartId: String

	val perfect: Int
	val good: Int
	val miss: Int

	val score: Int

	val uploadTime: OffsetDateTime

	val r: Int?

	val ranking: Int?

	val player: GameUser

}