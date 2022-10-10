package explode2.booster.graphql

import explode2.booster.graphql.definition.ExplodeMutation
import explode2.booster.graphql.definition.ExplodeQuery
import java.util.*
import kotlin.jvm.optionals.getOrNull

interface MazeProvider {

	val query: ExplodeQuery
	val mutation: ExplodeMutation

	companion object {

		/**
		 * 返回 [MazeProvider]，如果没有其他的，则返回 [BasicMaze] 提供的。
		 */
		@OptIn(ExperimentalStdlibApi::class)
		fun getProvider(): MazeProvider {
			return ServiceLoader.load(MazeProvider::class.java).findFirst().orElseGet { BasicMaze }
		}
	}
}