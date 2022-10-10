package explode2.labyrinth

import explode2.gateau.SongChart

interface SongChartFactory {

	fun getSongChartById(id: String): SongChart?

	fun createSongChart(difficultyClass: Int, difficultyValue: Int, id: String? = null, d: Double? = null): SongChart

}