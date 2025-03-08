package com.galacticware.griddle.domain.model.geometry

import com.galacticware.griddle.domain.model.error.Errors
import kotlinx.serialization.Serializable

/**
 * Represents the location of a side length of a grid box.
 * Defines the width or height in the correct location in 2d space.
 */
@Serializable
class StartAndSpan(
    val start: Int,
    val span: Int,
) {
    companion object {
        val originUnit = StartAndSpan(0, 1)
    }

    fun component1() = start
    fun component2() = span
    init {
        if (span < 1) {
            throw Errors.NEGATIVE_SPAN.send()
        }
    }

    override fun toString(): String = "{\"start\":$start,\"span\":$span}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StartAndSpan) return false

        if (start != other.start) return false
        if (span != other.span) return false

        return true
    }
    override fun hashCode(): Int {
        var result = start
        result = 31 * result + span
        return result
    }
}