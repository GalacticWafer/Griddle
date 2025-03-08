package com.galacticware.griddle.domain.model.geometry

import kotlinx.serialization.Serializable

/**
 * AxialParams represent length definition of one axis of a [KeyboardLayer].
 */
@Serializable
data class AxialParams(
    val cartesianAxis: CartesianAxis,
    val startAndSpan: StartAndSpan
) {
    fun times(i: Int): AxialParams {
        return AxialParams(cartesianAxis, StartAndSpan(startAndSpan.start, startAndSpan.span * i))
    }

    companion object {
        val oneUnitX = AxialParams(CartesianAxis.X, StartAndSpan.originUnit)
        val oneUnitY = AxialParams(CartesianAxis.Y, StartAndSpan.originUnit)
    }
    val span: Int get() = startAndSpan.span
    val start: Int get() = startAndSpan.start
    fun debugString(): String =
        "{\"cartesianType\":\"$cartesianAxis\",\"startAndSpan\":$startAndSpan}"

    override fun toString() = "{${cartesianAxis.alias}:$startAndSpan}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AxialParams) return false

        if (cartesianAxis != other.cartesianAxis) return false
        if (startAndSpan != other.startAndSpan) return false

        return true
    }
    override fun hashCode(): Int {
        var result = cartesianAxis.hashCode()
        result = 31 * result + startAndSpan.hashCode()
        return result
    }
}