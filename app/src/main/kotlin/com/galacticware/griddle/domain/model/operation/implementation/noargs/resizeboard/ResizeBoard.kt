package com.galacticware.griddle.domain.model.operation.implementation.noargs.resizeboard


import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.collection.ConcurrentStack
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.screen.NestedAppScreen

/**
 * This screen is activated when the the ResizeBoard EditorOperation is performed.
 * It allows the user to resize the board with the handle that appears above the board.
 * by dragging up/down on the handle, the width and height can be increased or decreased
 * proportionally.
 * If the user drags left/right, and the board doesn't occupy the entire width of the screen, then
 * the board will be shifted left or right accordingly.
 */
object ResizeBoard : Operation({ k -> ResizeBoard.executeOperation(k) }) {
    override val name get() = "Resize & Move keyboard"
    override val tag get() = OperationTag.RESIZE_BOARD
    override val menuItemDescription get() = "Move and resize the keyboard by dragging it"
    override val userHelpDescription get() = "Drag up or down to resize the keyboard. Drag left or right to move the keyboard."
    override val requiresUserInput: Boolean get() = false
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture =
        produceNewGestureWithAppSymbol(gesturePrototype, this, AppSymbol.RESIZE_BOARD)
    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
        = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to \"Resizing and move the board\"?")
        private var savedStack: ConcurrentStack<NestedAppScreen>? = null
        override fun executeOperation(keyboardContext: KeyboardContext) {
            synchronized(ResizeBoard) {
                val tempStack = ConcurrentStack<NestedAppScreen>()
                val keyboardStack = NestedAppScreen.stack
                while (keyboardStack.isNotEmpty()) {
                    tempStack.push(keyboardStack.pop()!!)
                    savedStack = tempStack
                }
            }
            Keyboard.isResizingAndMoving = true
            val context = keyboardContext.context
            Toast.makeText(context, "Resize and move the board by dragging it", Toast.LENGTH_LONG)
                .show()
        }
}