package explode2.booster.gatekeeper.command

import cn.taskeren.brigadierx.argument
import cn.taskeren.brigadierx.executesX
import cn.taskeren.brigadierx.literal
import cn.taskeren.brigadierx.register
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import explode2.booster.gatekeeper.GatekeeperPlugin
import explode2.booster.gatekeeper.GatekeeperSource

fun CommandDispatcher<GatekeeperSource>.addUserCommand(plugin: GatekeeperPlugin) = register("user") {

	val userRepo = plugin.userRepo

	argument("name", StringArgumentType.string()) {
		executesX {
			val id = StringArgumentType.getString(it, "name")
			val user = userRepo.getGameUserByName(id)
			if(user == null) {
				println("User not found!")
			} else {
				println("User: ${user.id}")
			}
		}

		literal("permission") {
			argument("permission-key", StringArgumentType.string()) {
				literal("get") {
					executesX {
						val id = StringArgumentType.getString(it, "name")
						val user = userRepo.getGameUserByName(id) ?: return@executesX println("User not found!")
						val permKey = StringArgumentType.getString(it, "permission-key")
						val hasPerm = user.hasPermission(permissionKey = permKey)
						if(hasPerm) {
							println("User has permission: $permKey")
						} else {
							println("User does not have permission: $permKey")
						}
					}
				}

				literal("grant") {
					executesX {
						val id = StringArgumentType.getString(it, "name")
						val user = userRepo.getGameUserByName(id) ?: return@executesX println("User not found!")
						val permKey = StringArgumentType.getString(it, "permission-key")
						user.grantPermission(permKey)
						println("Granted user permission: $permKey")
					}
				}

				literal("revoke") {
					executesX {
						val id = StringArgumentType.getString(it, "name")
						val user = userRepo.getGameUserByName(id) ?: return@executesX println("User not found!")
						val permKey = StringArgumentType.getString(it, "permission-key")
						user.revokePermission(permKey)
						println("Granted user permission: $permKey")
					}
				}

				literal("reset") {
					executesX {
						val id = StringArgumentType.getString(it, "name")
						val user = userRepo.getGameUserByName(id) ?: return@executesX println("User not found!")
						val permKey = StringArgumentType.getString(it, "permission-key")
						user.resetPermission(permKey)
						println("Granted user permission: $permKey")
					}
				}
			}

			executesX {
				println("/user <name> permission <permission> (get|grant|revoke|reset)")
			}
		}
	}

	executesX {
		println("/user <name> - check the basic info of the user")
	}
}