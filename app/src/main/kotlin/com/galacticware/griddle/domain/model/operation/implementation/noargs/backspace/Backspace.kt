package com.galacticware.griddle.domain.model.operation.implementation.noargs.backspace

import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyspammer.BackspaceSpammer
import com.galacticware.griddle.domain.model.modifier.AppModifierKey.Companion.control
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKey
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKeyArgs
import java.util.Timer
import java.util.TimerTask

/**
 * We don't want [Backspace] to inherit from [PressKey], because the number of logical checks
 * To Prevent this [Operation]'s behavior from needing sever preliminary checks before execution,
 * we distinguish the default backspace behavior from [PressKey], which otherwise calls the the same
 * inputConnection functions.
 *
 * I.E., this *would* be a [PressKey] if we had an interface and implementation for allowing
 * Backspace to inherit from [PressKey] or [PressKeyArgs].
 */
object Backspace: BaseBackspaceOperation({Backspace.executeOperation(it)}) {
    override fun executeOperation(keyboardContext: KeyboardContext) {
        keyboardContext.inputConnection.pressKey(BackspaceSpammer.backspaceDown, keyboardContext.gesture.assignment.modifiers)
    }
}
object HotSwapControlBackspace: BaseBackspaceOperation({Backspace.executeOperation(it)}) {
    var i = 0
    private var timer = Timer()
    override fun executeOperation(keyboardContext: KeyboardContext) {




        timer.cancel()
        keyboardContext.inputConnection.pressKey(BackspaceSpammer.backspaceDown, setOf(control))
        Keyboard.isHotSwapped = true
        timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    Keyboard.isHotSwapped = false
                }
            },
            500
        )
    }
}