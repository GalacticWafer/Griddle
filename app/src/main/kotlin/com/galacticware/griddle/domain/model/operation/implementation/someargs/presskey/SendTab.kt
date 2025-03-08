package com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey

import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.currentState
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.savedTextReplacement
import com.galacticware.griddle.domain.model.textreplacement.TextReplacement
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementUndoState.NONE
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag

val SendTab = object : Operation({}) {
    override val name: String by lazy { "Send a Tab character" }
    override var isBackspace: Boolean = false
    override val appSymbol by lazy { AppSymbol.TAB_RIGHT }
    override val menuItemDescription: String
        get() = "Send a literal Tab character."
    override val requiresUserInput: Boolean
        get() = false
    override val tag: OperationTag
        get() = OperationTag.SEND_TAB
    override val userHelpDescription: String
        get() = menuItemDescription
    override val shouldKeepDuringTurboMode: Boolean
        get() = true
    override fun executeOperation(keyboardContext: KeyboardContext) {
        savedTextReplacement?.let { TextReplacement.tryTextReplacementRedaction(keyboardContext);currentState = NONE }
            ?: run {
                val isShiftPressed = Keyboard.shiftState != ModifierKeyState.NONE
                if(isShiftPressed) {
                    keyboardContext.inputConnection.let {
//                        todo untab the text on the current line
                        /*it.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MOVE_HOME))

                        it.sendKeyEvent(KeyEvent(KeyEvent.KEYCODE_MOVE_HOME, KeyEvent.META_SHIFT_ON))
                        it.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_TAB, KeyEvent.META_SHIFT_ON))*/
                    }
                } else {
                    keyboardContext.inputConnection.commitText("\t", 1)
                }
                Keyboard.cancelNonRepeatingModifiers()
            }
    }
}