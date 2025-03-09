package com.galacticware.griddle.domain.model.shared

/**
 * We track the changes in direction over the duration of the touch event to determine the gesture.
 */
enum class Direction {
    EAST,
    NORTHEAST,
    NORTH,
    NORTHWEST,
    WEST,
    SOUTHWEST,
    SOUTH,
    SOUTHEAST,
    ;
    val prettyPrinted: String get() = when (this) {
        EAST -> "East"
        NORTHEAST -> "North-East"
        NORTH -> "North"
        NORTHWEST -> "North-West"
        WEST -> "West"
        SOUTHWEST -> "South-West"
        SOUTH -> "South"
        SOUTHEAST -> "South-East"
    }
}