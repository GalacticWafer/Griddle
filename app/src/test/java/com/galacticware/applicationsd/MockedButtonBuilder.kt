package com.galacticware.applicationsd

import androidx.compose.ui.unit.IntSize
import com.galacticware.griddle.domain.model.geometry.GridArea
import com.galacticware.griddle.domain.model.gesture.GestureAssignment
import com.galacticware.griddle.domain.model.button.GestureButtonBuilder
import com.galacticware.griddle.domain.model.operation.implementation.noargs.noop.NoOp

val MockedButtonBuilder = run {
    var builder = GestureButtonBuilder.gestureButton(
        rowStart = 1,
        colStart = 2,
        rowSpan = 3,
        colSpan = 4,
        gestureSet = mutableSetOf(),
        IntSize(10, 20),
        modifierTheme = MockedModifierThemeSet.none,
        settingsValueProvider = null,
        isPeripheral = false
    )
    AllGestureClasses.forEach { gestureClass ->
        val noOpAssignment = GestureAssignment(
            operation = NoOp,
            modifiers = emptySet(),
            isDisplayable = true,
            gridArea = GridArea.oneUnit,
            modifierThemeSet = MockedModifierThemeSet,
            appSymbol = null,
            isIndicator = false,
            argsJson = args,
        )
        builder = builder.withGesture(gestureClass.constructors.first().call(noOpAssignment))
    }
    builder
}