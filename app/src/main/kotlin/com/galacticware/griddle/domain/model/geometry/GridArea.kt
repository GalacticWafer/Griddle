package com.galacticware.griddle.domain.model.geometry

import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import kotlinx.serialization.Serializable

/**
 * Similar to how a gesture box defines its position in box units within the keyboard, a
 * KeyMap that will be displayed in some way will define its position within it's parent box.
 * Also like GestureButton, this area is always rectangular.
 */
@Serializable
data class GridArea(
    var gridPosition: GridPosition,
    var modifierTheme: ModifierTheme? = null,
) {
    /**
     * These are static references to the types of grid boxes that are common in the keyboard.
     */
    companion object {
        val wholeArea = GridArea(GridPosition.wholeAreaDefault)
        val oneUnit = GridArea(GridPosition.originUnit)
        val top = GridArea(
            GridPosition(
                rowParams = AxialParams.oneUnitY,
                colParams = AxialParams.oneUnitX.times(3),
            )
        )
        val centerWide = GridArea(
            GridPosition(
                rowParams = AxialParams(
                    CartesianAxis.Y, StartAndSpan(1, 1),
                ),
                colParams = AxialParams.oneUnitX.times(3),
            )
        )
        val bottom = GridArea(
            GridPosition(
                rowParams = AxialParams(
                    CartesianAxis.X, StartAndSpan(2, 1),
                ),
                colParams = AxialParams.oneUnitX.times(3),
            )
        )
        val left = GridArea(
            GridPosition(
                rowParams = AxialParams.oneUnitY.times(3),
                colParams = AxialParams.oneUnitX,
            )
        )
        val centerTall = GridArea(
            GridPosition(
                rowParams = AxialParams.oneUnitY.times(3),
                colParams = AxialParams(
                    CartesianAxis.X, StartAndSpan(1, 1),
                ),
            )
        )
        val right = GridArea(
            GridPosition(
                rowParams = AxialParams.oneUnitY.times(3),
                colParams = AxialParams(
                    CartesianAxis.Y, StartAndSpan(2, 1),
                ),
            )
        )
    }

    val rowStart: Int get() = gridPosition.rowStart
    val colStart: Int get() = gridPosition.colStart
    val rowSpan: Int get() = gridPosition.rowSpan
    val colSpan: Int get() = gridPosition.colSpan
    val position get() = gridPosition.rectangleLocation
    val size get() = gridPosition.size
    override fun toString(): String = "x:$colStart,y:$rowStart,w:$colSpan,h:$rowSpan"
}
