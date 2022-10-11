import explode2.booster.Booster.labyrinth
import org.junit.jupiter.api.Test

class TestGetAssessmentRecords {

	@Test
	fun testGetAssessmentRecords() {
		labyrinth.assessmentRecordFactory.getAssessmentRecords("3249a0da-221d-45d7-b9b1-81f3fbbe59b6").forEach(::println)
	}

}