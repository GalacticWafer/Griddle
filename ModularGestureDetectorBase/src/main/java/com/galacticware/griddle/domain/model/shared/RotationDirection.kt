package com.galacticware.griddle.domain.model.shared

/**
 * Enumerate the supported rotation directions for circle gestures.
 */
enum class RotationDirection {
    CLOCKWISE,
    ANTI_CLOCKWISE,
    ;
    val prettyPrinted: String get() = when (this) {
        CLOCKWISE -> "Clockwise"
        ANTI_CLOCKWISE -> "Counter-clockwise"
    }
}