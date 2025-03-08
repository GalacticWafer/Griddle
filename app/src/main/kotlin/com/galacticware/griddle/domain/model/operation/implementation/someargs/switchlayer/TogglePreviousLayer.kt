package com.galacticware.griddle.domain.model.operation.implementation.someargs.switchlayer

import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.operation.base.Operation

object TogglePreviousLayer : Operation({ keyboardContext: KeyboardContext ->
    TogglePreviousLayer.operate(
        keyboardContext
    )
}) {
    override fun executeOperation(keyboardContext: KeyboardContext) {
        operate(keyboardContext)
    }
    private fun operate(keyboardContext: KeyboardContext) {
        keyboardContext.keyboard.switchToPreviousLayer()
    }
}