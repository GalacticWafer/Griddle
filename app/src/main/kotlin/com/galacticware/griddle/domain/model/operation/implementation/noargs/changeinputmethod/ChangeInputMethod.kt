package com.galacticware.griddle.domain.model.operation.implementation.noargs.changeinputmethod

import android.content.Context
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import splitties.systemservices.inputMethodManager

/**
 * An operation to choose a different keyboard app.
 */
object ChangeInputMethod : Operation({ k -> ChangeInputMethod.executeOperation(k) }) {
    override val menuItemDescription: String
        get() = "Change IME"
    override val shouldKeepDuringTurboMode: Boolean
        get() = true
    override val tag: OperationTag
        get() = OperationTag.CHANGE_INPUT_METHOD
    override val appSymbol: AppSymbol
        get() = AppSymbol.CHOOSE_DIFFERENT_INPUT_METHOD
    override val userHelpDescription: String
        get() = "Use a different keyboard app 😢"
    override var isBackspace: Boolean
        get() = false
        set(value) {}
    override val requiresUserInput: Boolean
        get() = false
    override fun executeOperation(keyboardContext: KeyboardContext) {
        inputMethodManager.showInputMethodPicker()
        NestedAppScreen.stack.pop()
    }


    @Composable
    override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
            = ShowNoArgsConfirmationScreen(context, gesture, "")

    override fun produceNewGesture(gesturePrototype: Gesture): Gesture
            = produceNewGestureWithAppSymbol(gesturePrototype, this, appSymbol)
}