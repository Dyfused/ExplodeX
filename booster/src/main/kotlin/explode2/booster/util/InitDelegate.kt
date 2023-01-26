package explode2.booster.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 只能赋值一次的 Property Delegate
 */
@Suppress("UNCHECKED_CAST")
class SetOnceProperty<T> : ReadWriteProperty<Any, T> {

	private object EMPTY

	private var value: Any? = EMPTY

	override fun getValue(thisRef: Any, property: KProperty<*>): T {
		if(value == EMPTY) {
			throw IllegalStateException("Value isn't initialized")
		} else {
			return value as T
		}
	}

	override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
		if(this.value != EMPTY) {
			throw IllegalStateException("Value is initialized")
		}
		this.value = value
	}
}

inline fun <reified T> init(): ReadWriteProperty<Any, T> = SetOnceProperty()