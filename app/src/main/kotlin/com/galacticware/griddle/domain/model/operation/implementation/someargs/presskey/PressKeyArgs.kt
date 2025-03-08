package com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey


import android.view.KeyEvent
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import kotlinx.serialization.Serializable

@Serializable
data class PressKeyArgs(
    val keycode: Int,
    val respectShift: Boolean,
    val overrideMetaState: Boolean,
    val modifierKeys: Array<out ModifierKeyKind>,
): OperationArgs() {
    private val name by lazy { getKeyCodeName(keycode) }
    override fun description(): String = "Press $name"
    val causesTextReplacementRedaction: Boolean by lazy { keycode == KeyEvent.KEYCODE_DEL }

    override fun opInstance(): ParameterizedOperation<*> = PressKey

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PressKeyArgs

        if (keycode != other.keycode) return false
        if (respectShift != other.respectShift) return false
        if (overrideMetaState != other.overrideMetaState) return false
        if (!modifierKeys.contentEquals(other.modifierKeys)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keycode
        result = 31 * result + respectShift.hashCode()
        result = 31 * result + overrideMetaState.hashCode()
        result = 31 * result + modifierKeys.contentHashCode()
        return result
    }

    companion object {
        val instances: Set<PressKeyArgs> get() = setOf()
        fun getKeyCodeName(keycode: Int): String {
            return try {
                KeyEvent::class.java.fields
                    .firstOrNull { it.getInt(null) == keycode }?.name
                    ?: "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }
        }
    }
}
