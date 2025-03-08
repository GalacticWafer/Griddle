package com.galacticware.griddle.domain.model.gesture

import kotlinx.serialization.Serializable

/**
 * Base class for all gestures that can be performed on a [Keyboard].
 */
@Serializable
class GestureModel(
    var assignmentModel: GestureAssignmentModel,
    var swapAssignmentModel: GestureAssignmentModel?,
) {
    fun toGesture(gestureType: GestureType) = gestureType.withAssignment(assignmentModel.assignment,swapAssignmentModel?.assignment)
}
