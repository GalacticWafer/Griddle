package com.galacticware.griddle.domain.model.operation.base

import com.galacticware.griddle.domain.model.gesture.KeyboardContext

interface EditorOperationArgsContainer {
    fun executeOperation(keyboardContext: KeyboardContext)
    fun invoke()
    fun loadKeyboardContext(keyboardContext: KeyboardContext)
}