package com.galacticware.griddle.domain.model.geometry

/**
 * NaturalNumberSpan is a class that represents a consecutive span of positive integers, because
 * all span of zero is not allowed, and spans are always expressed as absolute values.
 */
import kotlinx.serialization.Serializable

@Serializable
class GridPosition(
    val rowParams: AxialParams,
    val colParams: AxialParams,
) {
    fun withPosition(rowStart: Int, colStart: Int): GridPosition {
        return GridPosition(
            AxialParams(rowParams.cartesianAxis, StartAndSpan(rowStart, rowParams.span)),
            AxialParams(colParams.cartesianAxis, StartAndSpan(colStart, colParams.span)),
        )
    }

    companion object {
        val originUnit = GridPosition(
            AxialParams.oneUnitX,
            AxialParams.oneUnitY,
        )

        /**
         * Default is a 3x3 grid
         */
        val wholeAreaDefault: GridPosition = originUnit.times(3)
    }

    private fun times(i: Int): GridPosition {
        return GridPosition(
            rowParams.times(i),
            colParams.times(i),
        )
    }

    val colSpan: Int get() = colParams.span
    val rowSpan: Int get() = rowParams.span
    val colStart: Int get() = colParams.start
    val rowStart: Int get() = rowParams.start
    val rectangleLocation get() = RectangleLocation(
        rowParams.start,
        colParams.start,
    )
    val size get() = RectangleSize(
        rowParams.span,
        colParams.span,
    )


    override fun toString(): String = " {" +
        "x:${rowParams.start}," +
        "y:${colParams.start}," +
        "w:${colParams.span}," +
        "h:${rowParams.span}" +
    "} "

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GridPosition) return false

        if (rowParams != other.rowParams) return false
        if (colParams != other.colParams) return false

        return true
    }
    override fun hashCode(): Int {
        var result = rowParams.hashCode()
        result = 31 * result + colParams.hashCode()
        return result
    }
}