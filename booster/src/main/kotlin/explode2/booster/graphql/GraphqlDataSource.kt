package explode2.booster.graphql

import explode2.labyrinth.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object GraphqlDataSource : KoinComponent {

	internal val assInfoRepo by inject<AssessmentInfoRepository>()
	internal val assRecRepo by inject<AssessmentRecordRepository>()
	internal val recRepo by inject<GameRecordRepository>()
	internal val songRepo by inject<SongSetRepository>()
	internal val userRepo by inject<GameUserRepository>()
	internal val chartRepo by inject<SongChartRepository>()

}