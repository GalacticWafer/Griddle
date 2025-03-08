package com.galacticware.griddle.domain.model.gesture

import com.galacticware.griddle.domain.model.shared.RotationDirection
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.operation.base.SavedExecution
import com.galacticware.griddle.domain.model.shared.GenericGestureType

data class GesturePerformanceInfo(
    var gesture: Gesture?,
    val previousOperation: SavedExecution,
    val swipeDirection: com.galacticware.griddle.domain.model.shared.Direction?,
    val genericGestureType: GenericGestureType,
    val buttonContainingFirstPoint: GestureButton,
    var buttonContainingLastPoint: GestureButton?,
    var rotationDirection: RotationDirection?,
) {
    fun withNewGesture(gesture: Gesture): GesturePerformanceInfo = this.copy(gesture = gesture)
}