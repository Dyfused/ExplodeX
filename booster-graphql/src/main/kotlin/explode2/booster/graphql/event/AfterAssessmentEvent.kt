package explode2.booster.graphql.event

import explode2.gateau.*

data class AfterAssessmentEvent(
	val user: GameUser,
	val assessment: Assessment,
	val record: AssessmentRecord
)
