package com.galacticware.griddle.domain.model.operation.base

import com.google.gson.Gson

abstract class OperationArgs {
    abstract fun description(): String
    abstract fun opInstance(): ParameterizedOperation<*>
    fun toJson(): String = Gson().toJson(this)
}