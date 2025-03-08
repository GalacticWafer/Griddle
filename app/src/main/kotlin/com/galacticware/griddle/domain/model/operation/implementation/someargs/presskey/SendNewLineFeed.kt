package com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey

import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.currentState
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.savedTextReplacement
import com.galacticware.griddle.domain.model.textreplacement.TextReplacement
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementUndoState.NONE

val SendNewLineFeed = object : Operation({}) {
    override fun executeOperation(keyboardContext: KeyboardContext) {
        savedTextReplacement?.let { TextReplacement.tryTextReplacementRedaction(keyboardContext);currentState = NONE }
            ?: run { keyboardContext.inputConnection.commitText("\n", 1) }
    }
    override var isBackspace: Boolean
        get() = super.isBackspace
        set(value) {}
    override val userHelpDescription: String get() = "Send a new line character"
    override val menuItemDescription: String get() = "Send a new line character"
    override val name: String get() = "sendNewLineFeed"
    override val requiresUserInput: Boolean get() = false
    override val shouldKeepDuringTurboMode: Boolean get() = true
    override val tag: OperationTag get() = OperationTag.NEW_LINE
}
