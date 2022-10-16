package explode2.booster.graphql.proxy

data class RemoteContext(
	var remoteServer: String,
	var remoteResource: String,
	var remoteSoudayo: String?,
)
