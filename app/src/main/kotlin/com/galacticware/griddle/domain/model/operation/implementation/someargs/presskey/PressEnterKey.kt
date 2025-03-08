package com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey

import android.content.Context
import android.view.KeyEvent
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag

object PressEnterKey : Operation({}) {
    override val name: String by lazy { "Press Enter" }
    override var isBackspace: Boolean = false
    override val appSymbol by lazy { AppSymbol.GO }
    override val menuItemDescription: String
        get() = "Press Enter with context sensitivity, such as the 'Next' of 'Go' actions."
    override val requiresUserInput: Boolean
        get() = false
    override val tag: OperationTag
        get() = OperationTag.ENTER
    override val userHelpDescription: String
        get() = "Press Enter"
    override val shouldKeepDuringTurboMode: Boolean
        get() = true
    override fun executeOperation(keyboardContext: KeyboardContext) {
        keyboardContext.inputConnection.editText.inputType
        keyboardContext.inputConnection.sendKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_ENTER
            )
        )
    }

    @Composable
    override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
    = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to ENTER?")
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture
            = produceNewGestureWithAppSymbol(gesturePrototype, this, appSymbol)
}