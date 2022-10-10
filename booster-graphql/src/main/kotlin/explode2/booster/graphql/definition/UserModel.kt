package explode2.booster.graphql.definition

import java.time.OffsetDateTime

data class UserModel(
	val _id: String,
	var username: String,
	var coin: Int?,
	var diamond: Int?,
	var PPTime: OffsetDateTime,
	val token: String,
	var RThisMonth: Int?,
	val access: UserAccessModel
)
