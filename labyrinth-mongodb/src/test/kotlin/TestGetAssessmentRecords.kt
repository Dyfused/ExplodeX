import explode2.labyrinth.LabyrinthPlugin
import org.junit.jupiter.api.Test

class TestGetAssessmentRecords {

	@Test
	fun testGetAssessmentRecords() {
		LabyrinthPlugin.labyrinth.assessmentRecordFactory.getAssessmentRecords("3249a0da-221d-45d7-b9b1-81f3fbbe59b6")
			.forEach(::println)
	}

}