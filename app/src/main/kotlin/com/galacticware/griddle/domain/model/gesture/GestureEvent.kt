package com.galacticware.griddle.domain.model.gesture

import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.shared.Direction
import com.galacticware.griddle.domain.model.shared.RotationDirection

data class GestureEvent(
    val gestureType: GestureType,
    val boxPositionAndSize: GridPosition,
    val swipeDirection: Direction?,
    val rotationDirection: RotationDirection?,
    val startTime: Long,
    val finishTime: Long,
    val acton: String,
)