package com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base

import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.modifier.ModifierAction
import com.galacticware.griddle.domain.model.modifier.ModifierAction.*
import com.galacticware.griddle.domain.model.modifier.ModifierCycleDirection
import com.galacticware.griddle.domain.model.modifier.ModifierCycleDirection.*
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind.ALT
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind.CONTROL
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind.SHIFT
import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import kotlinx.serialization.Serializable

@Serializable
data class ChangeModifierArgs(
    val modifierAction: ModifierAction,
    val modifierKeyKind: ModifierKeyKind,
    val cycleDirection: ModifierCycleDirection? = null,
): OperationArgs() {
    override fun description(): String = modifierAction.description(modifierKeyKind, cycleDirection)
    override fun opInstance(): ParameterizedOperation<*> = ChangeModifier
    override fun toString() = "$modifierAction $modifierKeyKind${cycleDirection?.let { " $it" }}"
    val label get() = modifierKeyKind.prettyName
    val appSymbol get() = if(modifierAction == RELEASE)
        AppSymbol.UNSHIFTED
    else
        modifierKeyKind.appSymbol
    companion object {
        val OneShotAlt by lazy { ChangeModifierArgs(ONE_SHOT, ALT) }
        val OneShotControl by lazy { ChangeModifierArgs(ONE_SHOT, CONTROL) }
        val OneShotShift by lazy { ChangeModifierArgs(ONE_SHOT, SHIFT) }
        val ReleaseAlt by lazy { ChangeModifierArgs(RELEASE, ALT) }
        val ReleaseControl by lazy { ChangeModifierArgs(RELEASE, CONTROL) }
        val ReleaseShift by lazy { ChangeModifierArgs(RELEASE, SHIFT) }
        val ToggleAltRepeat by lazy { ChangeModifierArgs(TOGGLE, ALT) }
        val ToggleControlRepeat by lazy { ChangeModifierArgs(TOGGLE, CONTROL) }
        val ToggleShiftRepeat by lazy { ChangeModifierArgs(TOGGLE, SHIFT) }
        val ForwardCycleShift by lazy { ChangeModifierArgs(CYCLE, SHIFT, FORWARD) }
        val ForwardCycleControl by lazy { ChangeModifierArgs(CYCLE, CONTROL, FORWARD) }
        val ForwardCycleAlt by lazy { ChangeModifierArgs(CYCLE, ALT, FORWARD) }
        val ReverseCycleShift by lazy { ChangeModifierArgs(CYCLE, SHIFT, REVERSE) }
        val ReverseCycleControl by lazy { ChangeModifierArgs(CYCLE, CONTROL, REVERSE) }
        val ReverseCycleAlt by lazy { ChangeModifierArgs(CYCLE, ALT, REVERSE) }
        val instances by lazy {
            setOf(
                ReleaseControl, ReleaseShift, ReleaseAlt,
                OneShotControl, OneShotShift, OneShotAlt,
                ToggleControlRepeat, ToggleShiftRepeat, ToggleAltRepeat,
                ReverseCycleShift, ReverseCycleControl, ReverseCycleAlt
            )
        }
    }
}