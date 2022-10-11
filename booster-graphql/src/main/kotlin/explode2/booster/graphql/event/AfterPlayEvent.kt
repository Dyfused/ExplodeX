package explode2.booster.graphql.event

import explode2.gateau.*

data class AfterPlayEvent(
	val user: GameUser,
	val chart: SongChart,
	val record: GameRecord
)
