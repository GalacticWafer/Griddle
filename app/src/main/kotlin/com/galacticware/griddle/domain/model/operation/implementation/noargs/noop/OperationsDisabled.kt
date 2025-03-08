package com.galacticware.griddle.domain.model.operation.implementation.noargs.noop

import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.operation.base.Operation

open class OperationsDisabled : Operation({}) {
    override fun executeOperation(keyboardContext: KeyboardContext) {}
}