package explode2.gateau

class Permission private constructor(val key: String, val default: Boolean) {

	override fun hashCode(): Int {
		return key.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		if(other !is Permission) return false
		return this.key == other.key && this.default == other.default
	}

	override fun toString(): String {
		return "Permission($key, default=$default)"
	}

	init {
		register(this)
	}

	companion object {
		private val GlobalPermissionRegistry = mutableListOf<Permission>()

		private fun find(permissionKey: String) =
			GlobalPermissionRegistry.firstOrNull { it.key == permissionKey }

		private fun register(p: Permission) {
			if(p !in GlobalPermissionRegistry) {
				GlobalPermissionRegistry += p
			} else {
				error("Duplicate key ${p.key} has been registered as ${find(p.key)}")
			}
		}

		/**
		 * Factory of [Permission], return the registered one, else construct a new one,
		 * so the [default] can be ignored as it was registered.
		 */
		fun getOrCreate(permissionKey: String, default: Boolean = false): Permission {
			return find(permissionKey) ?: Permission(permissionKey, default)
		}

		fun getOrNull(permissionKey: String): Permission? {
			return find(permissionKey)
		}
	}

}
