package graphql.event

import explode2.gateau.GameUser
import explode2.gateau.SongChart

data class BeforePlayEvent(
	val user: GameUser,
	val chart: SongChart
)
