package explode2.booster.bomb.submods.migrate

import explode2.booster.bomb.submods.user.RecordBO
import explode2.booster.bomb.submods.user.UserBO

internal data class UserMigrationBO(
    val user: UserBO,
    val records: List<RecordBO>
)
