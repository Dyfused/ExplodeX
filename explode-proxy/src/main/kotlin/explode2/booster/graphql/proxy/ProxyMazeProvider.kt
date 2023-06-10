package explode2.booster.graphql.proxy

import graphql.MazeProvider
import graphql.definition.ExplodeMutation
import graphql.definition.ExplodeQuery

class ProxyMazeProvider : MazeProvider {
	override val query: ExplodeQuery = ProxyMaze
	override val mutation: ExplodeMutation = ProxyMaze
}