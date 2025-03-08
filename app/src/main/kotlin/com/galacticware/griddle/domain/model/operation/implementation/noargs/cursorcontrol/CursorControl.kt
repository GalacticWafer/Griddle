package com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol

import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent.KEYCODE_FORWARD_DEL
import android.view.KeyEvent.KEYCODE_MOVE_END
import android.view.KeyEvent.KEYCODE_MOVE_HOME
import android.view.KeyEvent.KEYCODE_PAGE_UP
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import com.galacticware.griddle.domain.model.clipboard.GriddleClipboardItem
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.input.TextSelectionAnchor
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.create
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.pressKey
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.modifier.AppModifierKey.Companion.control
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.operation.implementation.noargs.backspace.Backspace
import com.galacticware.griddle.domain.view.composable.nestedappscreen.ClipboardScreen

private val boundaryRegex = """\s|[.,;:!?\-(){}\[\]@#$%^&*/|\\]""".toRegex()

fun findCursorStoppingPoints(input: CharSequence): List<Int> {
    val indices = mutableListOf<Int>()
    for ((index, char) in input.withIndex()) {
        if (char.isWhitespace() || boundaryRegex.matches("$char")) {
            indices.add(index)
        }
    }
    return indices
}

private fun checkSelection(
    keyboardContext: KeyboardContext,
): Pair<ExtractedText, CharSequence>? {
    val edit = keyboardContext.inputConnection.getExtractedText(ExtractedTextRequest(), 0) ?: return null
    if (Keyboard.shiftState == ModifierKeyState.NONE) {
        Keyboard.selectionAnchor = TextSelectionAnchor.NONE
    }
    return edit to edit.text
}


/**
 * A base class for all operations that move the cursor with arrow and navigation keys.
 */
abstract class CursorControl : Operation({}) {
    override val shouldKeepDuringTurboMode: Boolean = false
    override val requiresUserInput: Boolean
        get() = false
    override val userHelpDescription: String
        get() = menuItemDescription
}


/**
 * An operation to select all text (ctrl + a)
 */
object SelectAll : CursorControl() {
    override val tag get() = OperationTag.SELECT_ALL
    override val menuItemDescription: String
        get() = "Select all text"
    override fun executeOperation(keyboardContext: KeyboardContext) {
        val inputConnection = keyboardContext.inputConnection
        val edit = inputConnection.getExtractedText(ExtractedTextRequest(), 0)
        ClipboardScreen.wasLastOperationSelectAll = true
        inputConnection.setSelection(0, edit.text.length)
        // copy the text to the clipboard
        GriddleClipboardItem.pushSelectedTextData(keyboardContext.context, edit.text.toString())
    }
}

/**
 * An operation to delete one word to the left of the cursor.
 * We create [DeleteWordLeftGesture] in the exact same way as [pressKey]
 */
val DeleteWordLeftGesture = create(
    GestureType.BOOMERANG_LEFT,
    operation = Backspace,
    keycode = KEYCODE_DEL,
    appSymbol = AppSymbol.BACKSPACE,
    modifiers = setOf(control)
)
/* {
    override val userHelpDescription: String get() = "(CtRL+Left)"
    override val menuItemDescription: String get() = "Delete one word to the left"
    override val name: String get() = "DeleteWordLeft"
    override val tag get() = UserConfigurableOperationTag.DELETE_WORD_LEFT
    override val requiresUserInput: Boolean get() = false
    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        Button(
            onClick = {
                byokViewModel.setAskForConfirmation("Are you sure you want to change this gesture to \"Delete Word Left\"?")
            }
        ) {
            Text("Done")
        }
    }
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture {
        return gesture(
            GestureType.fromInstance(gesturePrototype),
            this,
            AppSymbol.DELETE_WORD_LEFT,
            foregroundColor = gesturePrototype.currentAssignment.currentTheme.primaryTextColor,
            backgroundColor = gesturePrototype.currentAssignment.currentTheme.primaryBackgroundColor,
            borderColor = gesturePrototype.currentAssignment.currentTheme.primaryBorderColor,
            modifierTheme = gesturePrototype.currentAssignment.currentTheme,
            modifiers = gesturePrototype.currentAssignment.modifiers,
            modifierThemeSet = gesturePrototype.currentAssignment.modifierThemeSet,
        )
    }
}*/
//object DeleteLeft: PressKey(KeyEvent.KEYCODE_DEL) {
//    override val userHelpDescription: String get() = "(Left)"
//    override val menuItemDescription: String get() = "Delete one character to the left"
//    override val name: String get() = "DeleteLeft"
//    override val tag get() = UserConfigurableOperationTag.DELETE_LEFT
//    override val requiresUserInput: Boolean get() = false
//    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture) {
//        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
//        Button(
//            onClick = {
//                byokViewModel.setAskForConfirmation("Are you sure you want to change this gesture to \"Delete Left\"?")
//            }
//        ) {
//            Text("Done")
//        }
//    }
//    override fun produceNewGesture(gesturePrototype: Gesture): Gesture {
//        return gesture(
//            GestureType.fromInstance(gesturePrototype),
//            this,
//            AppSymbol.BACKSPACE,
//            foregroundColor = gesturePrototype.currentAssignment.currentTheme.primaryTextColor,
//            backgroundColor = gesturePrototype.currentAssignment.currentTheme.primaryBackgroundColor,
//            borderColor = gesturePrototype.currentAssignment.currentTheme.primaryBorderColor,
//            modifierTheme = gesturePrototype.currentAssignment.currentTheme,
//            modifiers = gesturePrototype.currentAssignment.modifiers,
//            modifierThemeSet = gesturePrototype.currentAssignment.modifierThemeSet,
//        )
//    }
//}

/*
class MoveWordLeft : CursorControl() {
    override val userHelpDescription: String get() = "(CtRL+Left)"
    override val menuItemDescription: String get() = "Delete one word to the left"
    override val name: String get() = "DeleteWordLeft"
    override val tag get() = UserConfigurableOperationTag.DELETE_WORD_LEFT
    override val requiresUserInput: Boolean get() = false
    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        Button(
            onClick = {
                byokViewModel.setAskForConfirmation("Are you sure you want to change this gesture to Backspace?")
            }
        ) {
            Text("Done")
        }
    }
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture {
        return gesture(
            GestureType.fromInstance(gesturePrototype),
            this,
            AppSymbol.DELETE_WORD_LEFT,
            foregroundColor = gesturePrototype.currentAssignment.currentTheme.primaryTextColor,
            backgroundColor = gesturePrototype.currentAssignment.currentTheme.primaryBackgroundColor,
            borderColor = gesturePrototype.currentAssignment.currentTheme.primaryBorderColor,
            modifierTheme = gesturePrototype.currentAssignment.currentTheme,
            modifiers = gesturePrototype.currentAssignment.modifiers,
            modifierThemeSet = gesturePrototype.currentAssignment.modifierThemeSet,
        )
    }    override fun executeOperation(keyboardContext: KeyboardContext) = operate(keyboardContext)

    companion object {
        fun operate(keyboardContext: KeyboardContext) {
            val inputConnection = keyboardContext.inputConnection
            val (length, shouldBackspaceLetters, split) = calculateBackSpaceActions(
                 inputConnection
            ) ?: return
//            Log.d(TAG, "split: $split")
            var i = split.length - 1
            var index = length - 1
            while (i >= 0 && index >= 0) {
                val char = split[i]
//                Log.d(TAG, "char: $char")
                if (char.isLetterOrDigit() && shouldBackspaceLetters || !char.isLetterOrDigit() && !shouldBackspaceLetters) {
                    i--
                    index--
                }
            }
            inputConnection.deleteSurroundingText(length - index, length - 1)
        }
    }
}
*/

val DeleteWordRight =
    create(
        GestureType.BOOMERANG_LEFT,
        operation = Backspace,
        keycode = KEYCODE_FORWARD_DEL,
        appSymbol = AppSymbol.BACKSPACE,
        modifiers = setOf(control)
    )


val DeleteLeft = pressKey(GestureType.CLICK, keycode = KEYCODE_DEL, modifiers = setOf(control))
/*
object DeleteWordRight: PressKey(KeyEvent.KEYCODE_FORWARD_DEL, control) {
    override val userHelpDescription: String get() = "(CtRL+Right)"
    override val menuItemDescription: String get() = "Delete one word to the right"
    override val name: String get() = "DeleteWordRight"
    override val tag get() = UserConfigurableOperationTag.DELETE_WORD_RIGHT
    override val requiresUserInput: Boolean get() = false
    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        Button(
            onClick = {
                byokViewModel.setAskForConfirmation("Are you sure you want to change this gesture to \"Delete Word Right\"?")
            }
        ) {
            Text("Done")
        }
    }
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture {
        return gesture(
            GestureType.fromInstance(gesturePrototype),
            this,
            AppSymbol.DELETE_WORD_RIGHT,
            foregroundColor = gesturePrototype.currentAssignment.currentTheme.primaryTextColor,
            backgroundColor = gesturePrototype.currentAssignment.currentTheme.primaryBackgroundColor,
            borderColor = gesturePrototype.currentAssignment.currentTheme.primaryBorderColor,
            modifierTheme = gesturePrototype.currentAssignment.currentTheme,
            modifiers = gesturePrototype.currentAssignment.modifiers,
            modifierThemeSet = gesturePrototype.currentAssignment.modifierThemeSet,
        )
    }
}
*/

/*
class MoveWordRight : CursorControl() {
    override fun propertiesJson(): JsonObject = JsonObject().also {
         it.addProperty("type", this::class.qualifiedName) 
    }

    companion object {
        fun operate(keyboardContext: KeyboardContext) {
            val inputConnection = keyboardContext.inputConnection
            val edit = inputConnection.getExtractedText(
                ExtractedTextRequest(), 0
            )
            if (edit == null) {
                Unit
            } else if (edit.selectionEnd != edit.selectionStart) {
                inputConnection.deleteSurroundingText(
                    edit.selectionStart,
                    edit.selectionEnd
                )
            } else {
                var i = edit.selectionEnd
                val shouldBackspaceLetters = edit.text.length > edit.selectionEnd
                val reg = Regex("[${if (shouldBackspaceLetters) "" else "^"}a-zA-Z0-9]")
                while (i > 0 && edit.text[i].isWhitespace()) {
                    i++
                }
                while (i < edit.text.length && reg.matches("${edit.text[i]}")) {
                    i++
                }
                while (i < edit.text.length && edit.text[i].isWhitespace()) {
                    i++
                }
                inputConnection.setSelection(edit.selectionStart, i)
                listOf(KeyEvent.ACTION_DOWN, KeyEvent.ACTION_UP).map {
                    inputConnection.sendInput(
                        KeyEvent(it, KeyEvent.KEYCODE_DEL),
                        doNotCommitText = true
                    )
                }
            }
        }
    }
    override fun executeOperation(keyboardContext: KeyboardContext) = operate(keyboardContext)
}
*/


object MoveLeft : CursorControl() {
    override val tag get() = OperationTag.MOVE_LEFT
    override val menuItemDescription: String
        get() = "Move left one character"
        fun operate(keyboardContext: KeyboardContext) {
            val inputConnection = keyboardContext.inputConnection
            checkSelection(keyboardContext)?.let { (extractedText, _) ->
                if (TextSelectionAnchor.currentPosition == null && Keyboard.shiftState != ModifierKeyState.NONE) {
                    TextSelectionAnchor.currentPosition = extractedText.selectionStart
                }
                if(TextSelectionAnchor.currentPosition == null && extractedText.let{it.selectionStart != it.selectionEnd}) {
                    inputConnection.setSelection(extractedText.selectionStart, extractedText.selectionStart)
                    return
                }
                val previousIndexBeforeCurrentSelection = (extractedText.selectionStart - 1).coerceAtLeast(0)
                if (Keyboard.shiftState != ModifierKeyState.NONE) {
                    val position = TextSelectionAnchor.currentPosition!!
                    val hadNoPreviousSelection = extractedText.selectionStart == extractedText.selectionEnd
                    val cameFromTheRight = position > extractedText.selectionStart
                    val isReversingAndNowWantsToGoLeft = position < extractedText.selectionEnd
                    if (cameFromTheRight || hadNoPreviousSelection) {
                        inputConnection.setSelection(previousIndexBeforeCurrentSelection, extractedText.selectionEnd)
                    } else if (isReversingAndNowWantsToGoLeft) {
                        inputConnection.setSelection(extractedText.selectionStart, extractedText.selectionEnd - 1)
                    } else {
                        inputConnection.setSelection(previousIndexBeforeCurrentSelection, extractedText.selectionEnd)
                    }
                } else {
                    inputConnection.setSelection(previousIndexBeforeCurrentSelection, previousIndexBeforeCurrentSelection)
                }
            }
    }

    override fun executeOperation(keyboardContext: KeyboardContext) {
        operate(keyboardContext)
    }
}

object MoveRight : CursorControl() {
    override val tag get() = OperationTag.MOVE_RIGHT
    override val menuItemDescription: String
        get() = "Move right one character"
        fun operate(keyboardContext: KeyboardContext) {
            val inputConnection = keyboardContext.inputConnection
            checkSelection(keyboardContext)?.let { (extractedText, _) ->
                if (TextSelectionAnchor.currentPosition == null && Keyboard.shiftState != ModifierKeyState.NONE) {
                    TextSelectionAnchor.currentPosition = extractedText.selectionStart
                }
                if(TextSelectionAnchor.currentPosition == null && extractedText.let{it.selectionStart != it.selectionEnd}) {
                    inputConnection.setSelection(extractedText.selectionEnd, extractedText.selectionEnd)
                    return
                }
                val nextIndexAfterCurrentSelection = (extractedText.selectionEnd + 1).coerceAtMost(extractedText.text.length)
                if (Keyboard.shiftState != ModifierKeyState.NONE) {
                    val position = TextSelectionAnchor.currentPosition!!
                    val hadNoPreviousSelection = extractedText.selectionStart == extractedText.selectionEnd
                    val cameFromTheLeft = position < extractedText.selectionEnd
                    val isReversingAndNowWantsToGoRight = position > extractedText.selectionStart
                    if (cameFromTheLeft || hadNoPreviousSelection) {
                        inputConnection.setSelection(extractedText.selectionStart, nextIndexAfterCurrentSelection)
                    } else if (isReversingAndNowWantsToGoRight) {
                        inputConnection.setSelection(extractedText.selectionStart + 1, extractedText.selectionEnd)
                    } else {
                        inputConnection.setSelection(extractedText.selectionStart, nextIndexAfterCurrentSelection)
                    }
                } else {
                    inputConnection.setSelection(nextIndexAfterCurrentSelection, nextIndexAfterCurrentSelection)
                }
            }

    }

    override fun executeOperation(keyboardContext: KeyboardContext) = operate(keyboardContext)
}

object MoveWordLeft : CursorControl() {
    override val tag get() = OperationTag.MOVE_ONE_WORD_LEFT

    override fun executeOperation(keyboardContext: KeyboardContext) = operate(keyboardContext)
    override val menuItemDescription: String
        get() = "Move one word left"
        fun operate(keyboardContext: KeyboardContext) {
            val inputConnection = keyboardContext.inputConnection
            checkSelection(keyboardContext)?.let { (extractedText, chars) ->
                if (TextSelectionAnchor.currentPosition == null && Keyboard.shiftState != ModifierKeyState.NONE) {
                    TextSelectionAnchor.currentPosition = extractedText.selectionStart
                }
                if(TextSelectionAnchor.currentPosition == null && extractedText.let{it.selectionStart != it.selectionEnd}) {
                    inputConnection.setSelection(extractedText.selectionStart, extractedText.selectionStart)
                    return
                }
                val lineBreaks = findCursorStoppingPoints(chars)
                val previousIndexBeforeCurrentSelection = lineBreaks.let { indices ->
                    val lastOrNull = indices.lastOrNull { it < extractedText.selectionStart }
                    lastOrNull
                } ?: 0
                if (Keyboard.shiftState != ModifierKeyState.NONE) {
                    val position = TextSelectionAnchor.currentPosition!!
                    val hadNoPreviousSelection = extractedText.selectionStart == extractedText.selectionEnd
                    val cameFromTheRight = position > extractedText.selectionStart
                    val isReversingAndNowWantsToGoLeft = position < extractedText.selectionEnd
                    if (cameFromTheRight || hadNoPreviousSelection) {
                        inputConnection.setSelection(previousIndexBeforeCurrentSelection, extractedText.selectionEnd)
                    } else if (isReversingAndNowWantsToGoLeft) {
                        val nextIndexBeforeSelectionEnd = lineBreaks.let { indices ->
                            val lastOrNull = indices.lastOrNull { it < extractedText.selectionEnd }
                            lastOrNull
                        } ?: 0
                        inputConnection.setSelection(extractedText.selectionStart, nextIndexBeforeSelectionEnd)
                    } else {
                        inputConnection.setSelection(previousIndexBeforeCurrentSelection, extractedText.selectionEnd)
                    }
                } else {
                    inputConnection.setSelection(previousIndexBeforeCurrentSelection, previousIndexBeforeCurrentSelection)
                }
            }
        }
}

object MoveWordRight : CursorControl() {
    override val tag get() = OperationTag.MOVE_ONE_WORD_RIGHT
    override val menuItemDescription: String
        get() = "Move one word right"
    fun operate(keyboardContext: KeyboardContext) {
        val inputConnection = keyboardContext.inputConnection
        checkSelection(keyboardContext)?.let { (extractedText, chars) ->
            if(TextSelectionAnchor.currentPosition == null && Keyboard.shiftState != ModifierKeyState.NONE) {
                TextSelectionAnchor.currentPosition = extractedText.selectionStart
            }
            if(TextSelectionAnchor.currentPosition == null && extractedText.let{it.selectionStart != it.selectionEnd}) {
                inputConnection.setSelection(extractedText.selectionEnd, extractedText.selectionEnd)
                return
            }
            val lineBreaks = findCursorStoppingPoints(chars)
            val nextIndexAfterCurrentSelection = lineBreaks.let { indices ->
                val firstOrNull = indices.firstOrNull { it > extractedText.selectionEnd }
                firstOrNull
            }?.let { it + 1 } ?: extractedText.text.length
            if(Keyboard.shiftState != ModifierKeyState.NONE){
                val position = TextSelectionAnchor.currentPosition!!
                val hadNoPreviousSelection = extractedText.selectionStart == extractedText.selectionEnd
                val cameFromTheLeft = position < extractedText.selectionEnd
                val isReversingAndNowWantsToGoRight = position > extractedText.selectionStart
                if(cameFromTheLeft || hadNoPreviousSelection) {
                    inputConnection.setSelection(extractedText.selectionStart, nextIndexAfterCurrentSelection)
                } else if(isReversingAndNowWantsToGoRight) {
                    val nextIndexAfterSelectionStart = lineBreaks.let { indices ->
                        val firstOrNull = indices.firstOrNull { it > extractedText.selectionStart }
                        firstOrNull
                    }?.let { it + 1 } ?: extractedText.text.length
                    inputConnection.setSelection(nextIndexAfterSelectionStart, extractedText.selectionEnd)
                } else  {
                    inputConnection.setSelection(extractedText.selectionStart, nextIndexAfterCurrentSelection)
                }
            } else {
                inputConnection.setSelection(nextIndexAfterCurrentSelection, nextIndexAfterCurrentSelection)
            }
        }
    }

    override fun executeOperation(keyboardContext: KeyboardContext) = operate(keyboardContext)
}

object MoveHome : CursorControl() {
    override val menuItemDescription: String
        get() = "Move home"
    override val tag get() = OperationTag.MOVE_HOME
    override fun executeOperation(keyboardContext: KeyboardContext) {
        keyboardContext.inputConnection.sendKeyEvent(
            KeyEvent(
                /*down time*/ 0,
                /*event time*/ 0,
                /*action*/ KeyEvent.ACTION_DOWN,
                /*code*/ KEYCODE_MOVE_HOME,
                /*repeat*/ 0,
                /*meta state*/ keyboardContext.oneShotMetaState or Keyboard.currentMetaState.value
            )
        )
        Keyboard.cancelNonRepeatingModifiers()
    }
}

object MoveEnd: CursorControl() {
    override val menuItemDescription: String
        get() = "Move end"
    override val tag get() = OperationTag.MOVE_END
    override fun executeOperation(keyboardContext: KeyboardContext) {
        keyboardContext.inputConnection.sendKeyEvent(
            KeyEvent(
                /*down time*/ 0,
                /*event time*/ 0,
                /*action*/ KeyEvent.ACTION_DOWN,
                /*code*/ KEYCODE_MOVE_END,
                /*repeat*/ 0,
                /*meta state*/ keyboardContext.oneShotMetaState or Keyboard.currentMetaState.value
            )
        )
        Keyboard.cancelNonRepeatingModifiers()
    }
}

object MovePageUp: CursorControl() {
    override val menuItemDescription: String
        get() = "Move page up"
    override val tag get() = OperationTag.MOVE_PAGE_UP
    override fun executeOperation(keyboardContext: KeyboardContext) {
        keyboardContext.inputConnection.sendKeyEvent(
            KeyEvent(
                /*down time*/ 0,
                /*event time*/ 0,
                /*action*/ KeyEvent.ACTION_DOWN,
                /*code*/ KEYCODE_PAGE_UP,
                /*repeat*/ 0,
                /*meta state*/ keyboardContext.oneShotMetaState or Keyboard.currentMetaState.value
            )
        )
        Keyboard.cancelNonRepeatingModifiers()
    }
}

object MovePageDown: CursorControl() {
    override val menuItemDescription: String
        get() = "Move page down"
    override val tag get() = OperationTag.MOVE_PAGE_DOWN
    override fun executeOperation(keyboardContext: KeyboardContext) {
        keyboardContext.inputConnection.sendKeyEvent(
            KeyEvent(
                /*down time*/ 0,
                /*event time*/ 0,
                /*action*/ KeyEvent.ACTION_UP,
                /*code*/ KEYCODE_MOVE_END,
                /*repeat*/ 0,
                /*meta state*/ keyboardContext.oneShotMetaState or Keyboard.currentMetaState.value
            )
        )
        Keyboard.cancelNonRepeatingModifiers()
    }
}