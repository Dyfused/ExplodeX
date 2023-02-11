import explode2.labyrinth.LabyrinthPlugin
import java.util.*
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
internal class TestLabyrinth {

	private val uid = "1c2b86d9-b1f8-423f-9121-84d58dc37ba9"

	@Test
	fun testGenerateGameRecords() {
		val rec = LabyrinthPlugin.labyrinth.gameRecordFactory

		repeat(10) {
			val cid = createNewRandomChartId()
			repeat(10) {
				val x = rec.createGameRecord(cid, uid, 0, 0, 0, (0..1000000).random(), (0..1500).random())
				println(x.id)
			}
		}
	}

	@Test
	fun testUserCreate() {
		val u =
			LabyrinthPlugin.labyrinth.gameUserFactory.getGameUserById(uid) ?: LabyrinthPlugin.labyrinth.gameUserFactory
				.createGameUser("oscar", "oscars", uid)
		println(u)
	}

	@Test
	fun testTryGetUsersRValue() {
		val u = "1c2b86d9-b1f8-423f-9121-84d58dc37ba9"
		println(LabyrinthPlugin.labyrinth.gameUserFactory.getGameUserById(u)!!.calculateR())
	}
}

private val ValidChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
internal fun createNewRandomChartId() = List(24) { ValidChars.random() }.joinToString(separator = "")
internal fun createNewRandomUUID() = UUID.randomUUID().toString()