package com.galacticware.griddle.domain.model.operation.implementation.noargs.noop

import com.galacticware.griddle.domain.model.operation.base.OperationTag

object NoOp: OperationsDisabled() {
    override val tag get() = OperationTag.NO_OP
    override val menuItemDescription: String
        get() = "Do nothing"
    override val userHelpDescription: String
        get() = "Do nothing"
    override val name: String
        get() = "NoOp"
    override val requiresUserInput: Boolean
        get() = false
}