package com.galacticware.griddle.domain.model.operation.base

import com.galacticware.griddle.domain.model.appsymbol.AppSymbol

sealed class ExampleOperation: Operation({}) {
    override val menuItemDescription: String
        get() = ""
    override val shouldKeepDuringTurboMode: Boolean
        get() = false
    override val tag: OperationTag
        get() = OperationTag.NO_OP
    override val appSymbol: AppSymbol?
        get() = null
    override val userHelpDescription: String
        get() = ""
    override var isBackspace: Boolean
        get() = false
        set(value) {}
    override val requiresUserInput: Boolean
        get() = false
}