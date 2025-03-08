package com.galacticware.griddle.domain.model.operation.implementation.someargs.multioperation


/*
object MultiOperation : ParamerterizedOperaton<MultiOperationArgs>({ _: KeyboardContext -> }) {
    val childOps: MutableList<out Gesture> = mutableListOf()
    override val menuItemDescription: String
        get() = "Add operations to the circular list of operations for this multi-operation gesture."
    override val shouldKeepDuringTurboMode: Boolean
        get() = false
    override val tag: OperationTag
        get() = OperationTag.MULTI_OPERATION

    override fun provideArgs(jsonString: String): MultiOperationArgs {
        TODO("Not yet implemented")
    }

    @Composable
    override fun ShowArgsFinalizationScreen(context: Context, gesture: Gesture) {
        TODO("Not yet implemented")
    }

    override val appSymbol: AppSymbol?
        get() = null
    override val userHelpDescription: String
        get() = ""
    override var causesTextReplacementRedaction: Boolean
        get() = false
        set(value) {}
    override val requiresUserInput: Boolean
        get() = false

    var i = 0
    init {
        setMultiAssignment(0)
    }

    private fun setMultiAssignment(index: Int) {
        i = index
        allMultiAssignments[this] = childOps[index]
        causesTextReplacementRedaction = currentChildOperation.causesTextReplacementRedaction
    }

    val currentChildOperation get() = currentAssignment.operation
    val currentAssignment get() = currentChildGesture.assignment
    val currentChildGesture get() = childOps[i]

    fun swapOperations() {
        setMultiAssignment((i + 1) % childOps.size)
    }
    override fun executeOperation(keyboardContext: KeyboardContext) {
        operate(keyboardContext, this)
    }

        var savedOperation: SavedOperation? = null
        val allMultiAssignments = mutableMapOf<MultiOperation, Gesture>()
        private fun operate(kContext: KeyboardContext, multiOperation: MultiOperation) {
            val swappableGesture = allMultiAssignments[multiOperation]!!
            val f = {
                kContext.gesture.assignment = swappableGesture.currentAssignment
                swappableGesture.editorOperation.loadArgs(kContext)
                swappableGesture.editorOperation.invoke(
                    kContext.keyboard,
                    kContext.applicationContext,
                    kContext.gesture,
                    kContext.touchPoints,
                    kContext.view,
                    kContext.previousOperation,
                    kContext.gestureButtonPosition
                )
            }
            f()
            savedOperation = SavedOperation(swappableGesture.editorOperation, f)
        }
}
*/
