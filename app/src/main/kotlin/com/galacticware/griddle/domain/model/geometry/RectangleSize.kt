package com.galacticware.griddle.domain.model.geometry


/**
 * [RectangleSize] defines the width and height of a rectangle as two [StartAndSpan] objects [rowSpan] and [colSpan].
 */
class RectangleSize(
    val rowSpan: Int,
    val colSpan: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RectangleSize) return false

        if (rowSpan != other.rowSpan) return false
        if (colSpan != other.colSpan) return false

        return true
    }
    override fun hashCode(): Int {
        var result = rowSpan
        result = 31 * result + colSpan
        return result
    }
}