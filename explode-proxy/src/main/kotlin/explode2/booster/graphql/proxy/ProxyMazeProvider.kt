package explode2.booster.graphql.proxy

import explode2.booster.graphql.MazeProvider
import explode2.booster.graphql.definition.ExplodeMutation
import explode2.booster.graphql.definition.ExplodeQuery

class ProxyMazeProvider : MazeProvider {
	override val query: ExplodeQuery = ProxyMaze
	override val mutation: ExplodeMutation = ProxyMaze
}