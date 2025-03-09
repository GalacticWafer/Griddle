package com.galacticware.griddle.domain.model.shared.gesturedetection

import com.galacticware.griddle.domain.model.shared.Direction
import com.galacticware.griddle.domain.model.shared.GenericGestureType
import com.galacticware.griddle.domain.model.shared.Point
import com.galacticware.griddle.domain.model.shared.RotationDirection

interface IGestureDetector {
    /**
     * Perform whatever logic your detector needs to do when the user starts to perform a new
     * gesture (i.e., a pointer down event has occurred).
     */
    fun reset(): Unit? = null

    /**
     * Given a list of points representing a gesture, determine the type of gesture and the direction
     * if applicable.
     * @return a Triple of:
     * GenericGestureType roughly identifying the type of gesture.
     * Direction with the appropriate direction (or null if it was not a [GenericGestureType.SWIPE])
     * RotationDirection with the appropriate direction (or null if it was not a [GenericGestureType.CIRCLE])
     */
    fun determineGesture(
        minimumHoldTime: Int,
        minimumDragLength: Int,
        duration: Long,
        touchPoints: MutableList<Point>,
    ): Triple<GenericGestureType, Direction?, RotationDirection?>

    companion object {
        const val DEFAULT_MAXIMUM_DISTANCE_TO_REMOVE_JITTER = 15
    }
}
