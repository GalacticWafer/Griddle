package com.galacticware.griddle.domain.model.operation.implementation.noargs.repeat

import android.content.Context
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag

object Repeat : Operation({Repeat.executeOperation(it)}) {

    val startTime: Long = System.nanoTime()

    var repeatOp: Boolean = false
    override fun executeOperation(keyboardContext: KeyboardContext) {
        if(repeatOp) {
            return
        }
        repeatOp = true
        keyboardContext.previousOperation?.invoke()
        repeatOp = false
    }

    override val name get() = "Repeat"

    override val tag get() = OperationTag.REPEAT_PREVIOUS_OPERATION

    override val menuItemDescription get() = "Repeat action"

    override val userHelpDescription get() = "Repeat the previous gesture's action."

    override val requiresUserInput: Boolean get() = false

    override fun produceNewGesture(gesturePrototype: Gesture): Gesture =
        produceNewGestureWithAppSymbol(gesturePrototype, this, AppSymbol.REPEAT)

    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
            = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to \"Repeat the previous action\"?")

    override fun equals(other: Any?) = other is Repeat

    override fun hashCode(): Int { return startTime.hashCode() }

    private val recursionMap = mutableSetOf<Repeat>()
}