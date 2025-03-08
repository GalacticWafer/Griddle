package com.galacticware.griddle.domain.view.composable

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.galacticware.griddle.domain.model.shared.Point
import com.galacticware.griddle.domain.view.colorization.Hue

@Composable fun DrawGestureTrail(
    displayPoints: List<Point>,
    localDensity: Density,
    buttonOffsetX: Int,
    buttonOffsetY: Int,
    offsetY: Float,
    modifier1: Modifier,
) {
    displayPoints
        .plus(null as Point?)
        .windowed(2)
        .withIndex()
        .forEach { (i, points) ->
            val (p1, p2) = points
            val point = p1!!
            val (x, y) = with(localDensity) {
                (point.x + buttonOffsetX.dp.toPx()).toFloat() to
                        (point.y + buttonOffsetY.dp.toPx() + offsetY).toFloat()
            }
            Canvas(
                modifier = modifier1
            ) {
                val strokeWidth = 20f
                drawCircle(
                    color = Hue.HIGHLIGHTER_GREEN.hex,
                    center = Offset(x, y),
                    radius = (if (i == 0) 1.5f else 1f) * strokeWidth,
                    alpha = point.opacity,
                )
                // Draw the line segment (if there's a next point)
                p2?.let { nextPoint ->
                    val (nextX, nextY) = with(localDensity) {
                        Pair(
                            (nextPoint.x + buttonOffsetX.dp.toPx()).toFloat(),
                            (nextPoint.y + buttonOffsetY.dp.toPx() + offsetY).toFloat(),
                        )
                    }
                    drawLine(
                        color = Hue.GRELLOW.hex,
                        start = Offset(x, y),
                        end = Offset(nextX, nextY),
                        strokeWidth = strokeWidth,
                        alpha = (point.opacity + nextPoint.opacity) / 4f,
                    )
                }
            }
        }
}