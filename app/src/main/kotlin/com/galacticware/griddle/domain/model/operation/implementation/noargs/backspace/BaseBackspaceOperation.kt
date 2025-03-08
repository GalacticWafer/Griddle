package com.galacticware.griddle.domain.model.operation.implementation.noargs.backspace

import android.content.Context
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag

/**
 * Base class for all backspace-like operations
 */
abstract class BaseBackspaceOperation(f: (KeyboardContext) -> Unit) : Operation(f) {
    override var isBackspace: Boolean = true

    override val userHelpDescription: String = "Backspace"
    override val menuItemDescription: String = "Backspace"
    override val name: String = "Backspace"
    override val requiresUserInput: Boolean get() = true
    override val tag: OperationTag
        get() = OperationTag.BACKSPACE

    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
    = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to Backspace?")
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture
    = produceNewGestureWithAppSymbol(gesturePrototype, this, AppSymbol.BACKSPACE)
}
