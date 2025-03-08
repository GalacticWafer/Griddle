package com.galacticware.griddle.domain.model.operation.implementation.someargs.remappedsymbollookup

import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKeyArgs
import kotlinx.serialization.Serializable

@Serializable
data class RemappedSymbolLookupArgs(
    val label: AppSymbol? = null,
): OperationArgs() {
    private val name: String by lazy { PressKeyArgs.getKeyCodeName(AppSymbol.specialKeySymbols[label]!!) }
    override fun description(): String = "Press special key $name"
    override fun opInstance(): ParameterizedOperation<*> =
        RemappedSymbolLookup
}