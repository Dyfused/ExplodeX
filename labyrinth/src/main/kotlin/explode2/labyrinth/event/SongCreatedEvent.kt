package explode2.labyrinth.event

import explode2.gateau.SongChart
import explode2.gateau.SongSet

data class SongCreatedEvent(
	val set: SongSet,
	val charts: List<SongChart>
)