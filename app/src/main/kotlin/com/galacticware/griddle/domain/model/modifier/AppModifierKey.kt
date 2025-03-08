package com.galacticware.griddle.domain.model.modifier


/**
 * Class to represent modifier keys such as control, shift, and alt.
 */
class AppModifierKey(
    val kind: ModifierKeyKind,
    var state: ModifierKeyState = ModifierKeyState.NONE,
) {

    fun next(cycleDirection: ModifierCycleDirection = ModifierCycleDirection.FORWARD): ModifierKeyState {
        state = if (cycleDirection.isReversed) {
            ModifierKeyState.entries[(ModifierKeyState.entries.size + state.ordinal - 1) % ModifierKeyState.entries.size]
        } else state.next()
        return state
    }

    override fun toString(): String = "${kind.name}-$state"
    fun cancelOneShotModifier() {
        state = state.cancelOneShotModifier()
    }


    companion object {
        val shift = ModifierKeyKind.SHIFT
        val control = ModifierKeyKind.CONTROL
        val alt = ModifierKeyKind.ALT
    }
}