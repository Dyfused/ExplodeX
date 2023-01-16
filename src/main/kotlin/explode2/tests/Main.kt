package explode2.tests

import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Aggregates
import explode2.labyrinth.mongo.po.MongoGameRecord
import org.litote.kmongo.*

private val ThisDocument = "$$" + "ROOT"

fun main(args: Array<String>) {

	data class Omegas(val omegas: Int)

	val collGameRec = KMongo.createClient().getDatabase("explode").getCollection("GameRecords")

	collGameRec.aggregate<Omegas>(
		match(MongoGameRecord::playerId eq "3400cba2-cd86-43d3-ace4-0da1a92481c"),
		match(MongoGameRecord::score eq 1_000_000),
		group(MongoGameRecord::playedChartId, Accumulators.first("data", ThisDocument)),
		Aggregates.count("omegas")
	).toList().apply(::println).single().omegas
}