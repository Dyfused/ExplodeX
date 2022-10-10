package explode2.booster.graphql

import explode2.booster.graphql.definition.*
import explode2.gateau.*

fun GameUser.tunerize() = UserModel(this.id, this.username, this.coin, this.diamond, this.ppTime, this.id, this.calculateR(), UserAccessModel(this.isReviewer))

fun GameUser.tunerizePlayer() = PlayerModel(
	this.id,
	this.username,
	this.calculateHighestGoldenMedal(),
	this.calculateR()
)

fun SongSet.tunerize(relatedUser: GameUser? = null) = SetModel(
	this.id,
	this.introduction,
	this.coinPrice,
	NoterModel(this.noterName),
	this.musicName,
	this.musicComposer,
	this.playCount,
	this.charts.map(SongChart::tunerize),
	relatedUser?.let(::isUserGot) ?: false,
	this.state.isRanked
)

fun SongChart.tunerize() = ChartModel(
	this.id,
	this.difficultyClass,
	this.difficultyValue
)

fun AssessmentGroup.tunerize() = AssessmentGroupModel(
	this.id,
	this.name,
	this.assessments.map(Assessment::tunerize)
)

fun Assessment.tunerize() = AssessmentModel(
	this.id,
	this.medalLevel,
	this.healthBarLength,
	this.normalPassAccuracy,
	this.goldenPassAccuracy,
	this.exMissRate,
	this.assessmentCharts.map(AssessmentChart::tunerize),
	this.getAssessmentRecords(1, 0).map(AssessmentRecord::tunerizeRecs) // TODO: Validate if the return is correct
)

fun AssessmentChart.tunerize() = AssessmentChartModel(
	this.id,
	this.wrappingSet.tunerize()
)

fun AssessmentRecord.tunerize() = AssessmentRecordWithRankModel(
	this.player.tunerizePlayer(),
	this.ranking ?: 0,
	this.sumAccuracy,
	this.result,
	this.uploadTime
)

fun AssessmentRecord.tunerizeRecs() = AssessmentRecordsModel(
	this.sumAccuracy,
	true,
	listOfNotNull(*this.records.toTypedArray(), this.exRecord).map(AssessmentRecord.AssessmentRecordEntry::tunerize)
)

fun AssessmentRecord.AssessmentRecordEntry.tunerize() = AssessmentPlayRecordModel(
	this.perfect,
	this.good,
	this.miss,
	this.score
)

fun GameRecord.tunerize() = PlayRecordWithRankModel(
	player.tunerizePlayer(),
	PlayMod.Default,
	this.ranking ?: 0,
	this.score,
	this.perfect,
	this.good,
	this.miss,
	this.uploadTime
)