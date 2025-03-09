package com.galacticware.griddle.domain.model.gesture

import com.galacticware.griddle.domain.model.geometry.GridArea
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.modifier.ModifierThemeSet
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import com.galacticware.griddle.domain.model.operation.implementation.noargs.noop.NoOp

/**
 * A [GestureAssignment] defines what will happen when the [Gesture] is detected.
 */
class GestureAssignment(
    val operation: Operation,
    val modifiers: Set<ModifierKeyKind>, // hmmm... idk how to do this
    val isDisplayable: Boolean = true, // done
    val gridArea: GridArea, // done
    val modifierThemeSet: ModifierThemeSet, // done
    val appSymbol: AppSymbol? = null, // done
    val isIndicator: Boolean = false, // done
    val overrideMetaState: Boolean = false,
    val respectShift: Boolean = true,
    val isPeripheral: Boolean = false,
    val keycode: Int? = null,
    val argsJson: String?,
) {
    val model get() = GestureAssignmentModel(
        modifiers,
        isDisplayable,
        gridArea,
        modifierThemeSet,
        appSymbol,
        isIndicator,
        operation.tag,
        overrideMetaState,
        respectShift,
        isPeripheral,
        keycode,
        argsJson,
    )

    fun withOperation(operation: Operation): GestureAssignment {
        return GestureAssignment(
            operation,
            modifiers,
            isDisplayable,
            gridArea,
            modifierThemeSet,
            appSymbol,
            isIndicator,
            overrideMetaState,
            respectShift,
            isPeripheral,
            keycode,
            argsJson,
        )
    }

    fun withText(value: String): GestureAssignment {
        return GestureAssignment(
            operation,
            modifiers,
            isDisplayable,
            gridArea,
            modifierThemeSet.withTextTriple(value),
            appSymbol,
            isIndicator,
            overrideMetaState,
            respectShift,
            isPeripheral,
            keycode,
            argsJson,
        )
    }

    fun withSymbol(appSymbol: AppSymbol): GestureAssignment {
        return GestureAssignment(
            operation,
            modifiers,
            isDisplayable,
            gridArea,
            modifierThemeSet,
            appSymbol,
            isIndicator,
            overrideMetaState,
            respectShift,
            isPeripheral,
            keycode,
            argsJson,
        )
    }

    fun withKeycode(newKeycode: Int) = GestureAssignment(
        operation,
        modifiers,
        isDisplayable,
        gridArea,
        modifierThemeSet,
        appSymbol,
        isIndicator,
        overrideMetaState,
        respectShift,
        isPeripheral,
        newKeycode,
        argsJson,
    )

    fun withArgs(args: OperationArgs) = GestureAssignment (
        operation,
        modifiers,
        isDisplayable,
        gridArea,
        modifierThemeSet,
        appSymbol,
        isIndicator,
        overrideMetaState,
        respectShift,
        isPeripheral,
        keycode,
        args.toJson()
    )

    private val _modifierThemeSet get() = modifierThemeSet
    val currentTheme get() = _modifierThemeSet.currentTheme()
    val noneTheme get() = _modifierThemeSet.none
    val onceTheme get() = _modifierThemeSet.once
    val repeatTheme get() = _modifierThemeSet.repeat
    val currentText get() = appSymbol?.value ?: _modifierThemeSet.currentText()
    val modifierKeyKind get() = _modifierThemeSet.modifierKeyKind

    val shiftState: ModifierKeyState? =
        modifiers.firstOrNull { it == ModifierKeyKind.SHIFT }?.let { ModifierKeyState.OFF }
    val ctrlState: ModifierKeyState? =
        modifiers.firstOrNull { it == ModifierKeyKind.CONTROL }?.let { ModifierKeyState.OFF }
    val altState: ModifierKeyState? =
        modifiers.firstOrNull { it == ModifierKeyKind.ALT }?.let { ModifierKeyState.OFF }

    companion object {
        val EMPTY_ASSIGNMENT: GestureAssignment = GestureAssignment(
            NoOp,
            setOf(),
            isDisplayable = true,
            GridArea(GridPosition.originUnit, ModifierTheme.BLANK),
            ModifierThemeSet.allSameText("", ModifierTheme.BLANK),
            appSymbol = null,
            argsJson = null,
        )
    }
}