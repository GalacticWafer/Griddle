package com.galacticware.griddle.domain.model.geometry


/**
 * [RectangleLocation] defines the top-left point of a rectangle as [rowStart] and [colStart].
 * Basically Points that only have integers.
 */
class RectangleLocation(
    var rowStart: Int,
    var colStart: Int,
) {
    companion object {
        val wholeArea = RectangleLocation(3, 3)
        val topLeft = RectangleLocation(0, 0)
        val topCenter = RectangleLocation(0, 1)
        val topRight = RectangleLocation(0, 2)
        val left = RectangleLocation(1, 0)
        val center = RectangleLocation(1, 1)
        val right = RectangleLocation(1, 2)
        val bottomLeft = RectangleLocation(2, 0)
        val bottomCenter = RectangleLocation(2, 1)
        val bottomRight = RectangleLocation(2, 2)
    }

    override fun toString(): String {
        return "$rowStart,$colStart"
    }
    val json get(): String = "{\"rowStart\":$rowStart,\"colStart\":$colStart}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RectangleLocation) return false

        if (rowStart != other.rowStart) return false
        if (colStart != other.colStart) return false

        return true
    }
    override fun hashCode(): Int {
        var result = rowStart
        result = 31 * result + colStart
        return result
    }
}