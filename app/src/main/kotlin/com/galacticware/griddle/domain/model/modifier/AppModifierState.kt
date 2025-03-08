package com.galacticware.griddle.domain.model.modifier

import android.view.KeyEvent
import com.galacticware.griddle.domain.model.input.TextSelectionAnchor

/**
 * Represents the state of the modifier keys on the keyboard.
 */
class AppModifierState(
    shift: ModifierKeyState = ModifierKeyState.NONE,
    ctrl: ModifierKeyState = ModifierKeyState.NONE,
    alt: ModifierKeyState = ModifierKeyState.NONE
) {
    val value: Int get() = run {
        listOf(
            shift to KeyEvent.META_SHIFT_ON,
            ctrl to KeyEvent.META_CTRL_ON,
            alt to KeyEvent.META_ALT_ON,
        ).fold(0) { accumulator, it ->
            accumulator or if(it.first != ModifierKeyState.NONE) it.second else 0
        }
    }

    /**
     * or operator for combining the current state with the given [int].
     */
    infix fun or(int: Int) : Int = value or int

    /**
     * Modify the given kind, then return the new modifier state.
     */
    fun cycleToNextModifierKeyStateFor(
        modifierKeyKind: ModifierKeyKind,
        cycleDirection: ModifierCycleDirection
    ) = when(modifierKeyKind) {
        ModifierKeyKind.SHIFT -> _shift.next(cycleDirection)
        ModifierKeyKind.CONTROL -> _ctrl.next(cycleDirection)
        ModifierKeyKind.ALT -> _alt.next(cycleDirection)
    }

    /**
     * Turn all modifiers that are not set to [ModifierKeyState.REPEAT] to [ModifierKeyState.NONE].
     */
    fun cancelOneShotModifiers() {
        _shift.cancelOneShotModifier()
        _ctrl.cancelOneShotModifier()
        _alt.cancelOneShotModifier()
        TextSelectionAnchor.currentPosition = null
    }

    private val _shift: AppModifierKey = AppModifierKey(ModifierKeyKind.SHIFT)
    private val _ctrl: AppModifierKey = AppModifierKey(ModifierKeyKind.CONTROL)
    private val _alt: AppModifierKey = AppModifierKey(ModifierKeyKind.ALT)

    val shift get() = run {
        val state = _shift.state
        if(state == ModifierKeyState.NONE) TextSelectionAnchor.currentPosition = null
        state
    }
    val ctrl get() = _ctrl.state
    val alt get() = _alt.state

    init {
        _shift.state = shift
        _ctrl.state = ctrl
        _alt.state = alt
    }
}