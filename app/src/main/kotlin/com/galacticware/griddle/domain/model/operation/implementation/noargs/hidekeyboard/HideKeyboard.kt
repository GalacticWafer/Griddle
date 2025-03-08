package com.galacticware.griddle.domain.model.operation.implementation.noargs.hidekeyboard

import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.input.IMEService
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.view.KeyboardView

/**
 * An operation to hide this IME keyboard
 */
object HideKeyboard : Operation({ k -> HideKeyboard.executeOperation(k) }) {
    override val menuItemDescription: String
        get() = "Hide the keyboard"
    override val shouldKeepDuringTurboMode: Boolean
        get() = true
    override val tag: OperationTag
        get() = OperationTag.CHANGE_INPUT_METHOD
    override val appSymbol: AppSymbol
        get() = AppSymbol.CHOOSE_DIFFERENT_INPUT_METHOD
    override val userHelpDescription: String
        get() = menuItemDescription
    override var isBackspace: Boolean
        get() = false
        set(value) {}
    override val requiresUserInput: Boolean
        get() = false
    override fun executeOperation(keyboardContext: KeyboardContext) {
        // todo, this doesn't work
//        KeyboardView.isVisible = false
    }
}