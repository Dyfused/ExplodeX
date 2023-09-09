package explode2.booster.graphql.event

import explode2.gateau.Assessment
import explode2.gateau.GameUser

data class BeforeAssessmentEvent(
	val user: GameUser,
	val assessment: Assessment
)
