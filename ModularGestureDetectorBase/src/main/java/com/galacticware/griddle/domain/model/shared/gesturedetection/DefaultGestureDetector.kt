package com.galacticware.griddle.domain.model.shared.gesturedetection



import com.galacticware.griddle.domain.model.shared.Direction
import com.galacticware.griddle.domain.model.shared.GenericGestureType
import com.galacticware.griddle.domain.model.shared.Point
import kotlin.math.abs
import kotlin.math.hypot
import com.galacticware.griddle.domain.model.shared.RotationDirection as RotationDirection1

/**
 * Warning: This gesture detector does not detect [GenericGestureType.CIRCLE] gestures.
 */
object DefaultGestureDetector : IGestureDetector {
   private const val JITTER_REMOVAL_DISTANCE = 15
    override fun reset() {}

    override fun determineGesture(
        minimumHoldTime: Int,
        minimumDragLength: Int,
        duration: Long,
        touchPoints: MutableList<Point>,
    ): Triple<GenericGestureType, Direction?, RotationDirection1?> {
        if(touchPoints.isEmpty()) throw Exception()

        val points = touchPoints.dropLast(1)
            .zip(touchPoints.drop(1))
            .map {( p1, p2) -> hypot(p1.x - p2.x, p1.y - p2.y) }
            .withIndex()
            .filter {
                val isThisPairJittery = it.value <= JITTER_REMOVAL_DISTANCE
                val isLastPair = it.index == touchPoints.size - 1
                val hasAtLeastThreePairs = touchPoints.size > 3
                !isThisPairJittery || !hasAtLeastThreePairs || isLastPair
            }
            .map { touchPoints[it.index] }
        val triples = points.withIndex().map { (it, x) ->
            val p2 = points[it]
            val d = hypot(points.first().x - p2.x, points.first().y - p2.y)
            Triple(it, x, d)
        }

        val distanceTraveled = triples.sumOf { it.third }

        if(points.size < 2 || distanceTraveled < minimumDragLength) {
            return Triple(
                if(duration > minimumHoldTime) GenericGestureType.HOLD else GenericGestureType.CLICK,
                null,
                null
            )
        }
        val farPointTriple = triples.maxBy { it.third }
        val (diffX, diffY) = (farPointTriple.second to points.first())
            .let { (b, a) ->
                val diffX = b.x - a.x
                val diffY = b.y - a.y
                diffX to diffY
            }

        return Triple(
            if(farPointTriple.second != points.last() && triples.slice(0 until farPointTriple.first).sumOf { it.third } >=  .5 * triples.slice(farPointTriple.first until points.size).sumOf { it.third }) GenericGestureType.BOOMERANG else GenericGestureType.SWIPE,
            if(abs(diffX) > abs(diffY/2) && abs(diffY) > abs(diffX/2)) {
                // diagonal
                if(diffX > 0 && diffY > 0) {
                    Direction.SOUTHEAST
                } else if(diffX > 0 && diffY < 0) {
                    Direction.NORTHEAST
                } else if(diffX < 0 && diffY > 0) {
                    Direction.SOUTHWEST
                } else {
                    Direction.NORTHWEST
                }
            } else {
                // cardinal
                if (abs(diffX) > abs(diffY)) {
                    if (diffX > 0) {
                        Direction.EAST
                    } else {
                        Direction.WEST
                    }
                } else {
                    if (diffY > 0) {
                        Direction.SOUTH
                    } else {
                        Direction.NORTH
                    }
                }
            }, null)
    }
}