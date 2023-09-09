package explode2.booster.graphql.event

import explode2.gateau.GameRecord
import explode2.gateau.GameUser
import explode2.gateau.SongChart

data class AfterPlayEvent(
	val user: GameUser,
	val chart: SongChart,
	val record: GameRecord
)
