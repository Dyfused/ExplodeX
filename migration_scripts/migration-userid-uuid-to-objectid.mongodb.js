/*

    Migration script to change User._id to ObjectId

    Usage:
        1. Modify config below this text
        2. Copy the whole script and run in Mongo Shell
        3. Replace Users collection with UsersMigrated collection manually
    
    Author: Taskeren

    This script changes a lot of things, as you should be careful about it!

*/

// edit here ->
const config = {
    db_name: "expX",
}
// <- edit end

use(config.db_name)

var success_count = 0
var error_count = 0

db.Users.find().forEach(user => {
    try {
        const user_id = user._id

        const new_id = ObjectId()

        // Insert updated user
        db.UsersMigrated.insertOne({
            ...user,
            _id: new_id
        })

        // Update game records
        db.GameRecords.updateMany({ playerId: user_id }, {
            $set: {
                playerId: new_id
            }
        })

        // Update assessment records
        db.AssessRecords.updateMany({ playerId: user_id }, {
            $set: {
                playerId: new_id
            }
        })

        // Update binding noter
        db.Sets.updateMany({ noterUserId: user_id }, {
            $set: {
                noterUserId: new_id
            }
        })

        console.log(`Updated User(old=${user_id}, new=${new_id}, name=${user.username})`)

        success_count++
    } catch (ex) {
        error_count++
        console.log(`Error to update User(old=${user_id}, new=${new_id}, name=${user.username})`)
    }
    
})

console.log(`Finished migration, ${success_count} successes, ${error_count} failures.`)
