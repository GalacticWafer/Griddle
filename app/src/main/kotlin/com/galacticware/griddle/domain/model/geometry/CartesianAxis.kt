package com.galacticware.griddle.domain.model.geometry

/**
 * Simple enum to orient a StartAndSpan object along one of the cartesian axis.
 */
enum class CartesianAxis(
    val alias: String
) {
    X("horizontal"),
    Y("vertical"),;
}