package com.galacticware.griddle.domain.model.usercontolled.userdefinedgesturemapping

import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationArgs

data class ReassignmentData(
    val draftGesture: Gesture,
    val operation: Operation,
    val args: OperationArgs? = null,
)
