package explode2.labyrinth

import explode2.gateau.GameUser

interface GameUserRepository {

	fun getGameUserById(id: String): GameUser?

	fun getGameUserByIdThin(id: String): GameUser?

	fun getGameUserByName(username: String): GameUser?

	fun createGameUser(username: String, password: String, id: String? = null): GameUser

}