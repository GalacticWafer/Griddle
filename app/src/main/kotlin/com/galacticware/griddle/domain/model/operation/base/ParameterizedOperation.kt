package com.galacticware.griddle.domain.model.operation.base

import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.gesture.VariableOperation
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol

abstract class ParameterizedOperation<T: OperationArgs>(
    f: (KeyboardContext) -> Unit
):  ComplexArgsProvider<T>, VariableOperation(f) {
    override val menuItemDescription: String get() = throwUnsupported()
    override val shouldKeepDuringTurboMode: Boolean get() = throwUnsupported()
    override val tag: OperationTag get() = throwUnsupported()
    override val appSymbol: AppSymbol? get() = throwUnsupported()
    override val userHelpDescription: String get() = throwUnsupported()
    override val requiresUserInput: Boolean get() = throwUnsupported()
}