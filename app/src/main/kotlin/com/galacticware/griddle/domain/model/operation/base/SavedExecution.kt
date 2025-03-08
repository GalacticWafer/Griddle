package com.galacticware.griddle.domain.model.operation.base

class SavedExecution(
    val op: Operation,
    val loadedFunction: ()  -> Unit,
): () -> Unit {
    override fun invoke() {
        loadedFunction.invoke()
    }
}