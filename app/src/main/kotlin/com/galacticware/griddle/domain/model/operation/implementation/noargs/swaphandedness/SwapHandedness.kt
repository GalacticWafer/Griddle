package com.galacticware.griddle.domain.model.operation.implementation.noargs.swaphandedness

import android.content.Context
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag


object SwapHandedness: Operation({ k -> SwapHandedness.executeOperation(k) }) {
    override val name get() = "Swap handedness"
    override val tag get() = OperationTag.SWAP_HANDEDNESS
    override val menuItemDescription get() = "Swap handedness"
    override val userHelpDescription get() = "Change the keyboard from left-handed to right-handed and vice versa"
    override val requiresUserInput get() = false
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture =
        produceNewGestureWithAppSymbol(gesturePrototype, this, AppSymbol.SWAP_HANDEDNESS)
    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
            = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to \"Swap handedness\"?")

    override fun executeOperation(keyboardContext: KeyboardContext) {
        keyboardContext.keyboard.swapHandedness()
    }
}