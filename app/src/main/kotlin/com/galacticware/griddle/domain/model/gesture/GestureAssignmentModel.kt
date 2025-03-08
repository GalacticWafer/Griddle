package com.galacticware.griddle.domain.model.gesture

import com.galacticware.griddle.domain.model.geometry.GridArea
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierThemeSet
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import kotlinx.serialization.Serializable

@Serializable
data class GestureAssignmentModel(
    val modifiers: Set<ModifierKeyKind>,
    val isDisplayable: Boolean,
    val gridArea: GridArea,
    val modifierThemeSet: ModifierThemeSet,
    val appSymbol: AppSymbol?,
    val isIndicator: Boolean,
    val operationArgs: OperationTag,
    val overrideMetaState: Boolean,
    val respectShift: Boolean,
    val isPeripheral: Boolean,
    val keycode: Int? = null,
    val args: String? = null,
) {
    val assignment get() = run {
        GestureAssignment(
            operationArgs.objectInstance,
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
            args,
        )
    }
}
