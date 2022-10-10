package explode2.labyrinth

import explode2.gateau.*

interface AssessmentInfoFactory {

	@Deprecated("Use getAssessmentGroups", replaceWith = ReplaceWith("getAssessmentGroups()[0]"))
	val singletonAssessmentGroup: AssessmentGroup

	@Suppress("DEPRECATION")
	fun getAssessmentGroups(): List<AssessmentGroup> = listOf(singletonAssessmentGroup)

	fun getAssessmentGroupById(gId: String): AssessmentGroup?

	fun getAssessmentById(id: String): Assessment?

}