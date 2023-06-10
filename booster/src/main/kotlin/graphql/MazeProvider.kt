package graphql

import explode2.booster.ExplodeService
import graphql.definition.ExplodeMutation
import graphql.definition.ExplodeQuery

interface MazeProvider {

	val query: ExplodeQuery
	val mutation: ExplodeMutation

	companion object {

		/**
		 * 返回 [MazeProvider]，如果没有其他的，则返回 [BasicMaze] 提供的。
		 */
		fun getProvider(): MazeProvider {
			return ExplodeService.load<MazeProvider>().findFirst().orElseGet { BasicMaze }
		}
	}
}