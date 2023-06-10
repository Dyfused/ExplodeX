package graphql.event

import explode2.gateau.Assessment
import explode2.gateau.AssessmentRecord
import explode2.gateau.GameUser

data class AfterAssessmentEvent(
	val user: GameUser,
	val assessment: Assessment,
	val record: AssessmentRecord
)
