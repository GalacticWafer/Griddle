package com.galacticware.applicationsd

import android.content.Context
import com.galacticware.griddle.domain.model.shared.GenericGestureType
import com.galacticware.griddle.domain.model.gesture.GestureAssignment
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.KeyboardKind
import com.galacticware.griddle.domain.model.operation.implementation.noargs.simpleinput.SimpleInput
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.mockito.Mockito.mock


class KeyboardTest {
    @Test
    fun testRemapSimpleInput() {
        val context = mock(Context::class.java)
        val mockLayer = MockKeyboardLayer()
        val layers = setOf(mockLayer)
        val keyboard = Keyboard(context, layers, keyboardKind = KeyboardKind.SINGLE_BUTTON_DESIGNER_MODE)
        val button = keyboard.primaryLayer.gestureButtonBuilders.first()()
        val gestures = keyboard.primaryLayer.gestureButtonBuilders.first().gestureSet

        val gesturesWithClasses = gestures.associateWith {  SimpleInput() }
        val newText = "the"
        gesturesWithClasses.forEach { (gesture, operation) ->
            val operationClassBefore = gesture.currentAssignment.operation::class
            val gestureWithDifferentOperation = gesture::class.constructors.first().call(
                GestureAssignment(
                    operation = operation,
                    gestureButtonType = gesture.currentAssignment.gestureButtonType,
                    modifiers = gesture.currentAssignment.modifiers,
                    isDisplayable = gesture.currentAssignment.isDisplayable,
                    gridArea = gesture.currentAssignment.gridArea,
                    modifierThemeSet = gesture.currentAssignment.modifierThemeSet.withTextTriple(newText, newText, newText),
                    appSymbol = gesture.currentAssignment.appSymbol,
                    isIndicator = gesture.currentAssignment.isIndicator,
                )
            )
            keyboard.updateGesture(gestureWithDifferentOperation, GenericGestureType.fromInstance(gesture), button)
            keyboard.primaryLayer.gestureButtonBuilders.forEach { b ->
                val g = b.gestureSet.first { g ->
                    g::class == gesture::class
                }
                if(g::class == gesture::class) {
                    val operationClassAfter = g.currentAssignment.operation::class
                    assertNotEquals(operationClassBefore, operationClassAfter)
                }
            }
        }
    }
}