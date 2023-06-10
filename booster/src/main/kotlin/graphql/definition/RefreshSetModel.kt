package graphql.definition

import com.expediagroup.graphql.generator.annotations.GraphQLName

@GraphQLName("Set")
data class RefreshSetModel(
	val _id: String,
	val isRanked: Boolean,
	val introduction: String,
	val noterName: String,
	val musicTitle: String
)