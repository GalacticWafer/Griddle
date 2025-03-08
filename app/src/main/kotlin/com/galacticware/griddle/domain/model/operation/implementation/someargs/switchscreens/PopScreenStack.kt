package com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens

import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.screen.NestedAppScreen

object PopScreenStack : Operation({}) {
    override val appSymbol get() = AppSymbol.GO_BACK_ENGLISH
    override fun executeOperation(keyboardContext: KeyboardContext) {
        NestedAppScreen.stack.pop()
    }
}