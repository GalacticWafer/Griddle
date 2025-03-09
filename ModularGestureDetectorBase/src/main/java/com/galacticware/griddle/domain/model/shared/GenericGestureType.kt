package com.galacticware.griddle.domain.model.shared

/**
 * The kinds of gestures the keyboard can detect, some of which are abstract.
 * This logical grouping mirrors our heuristic approach to gesture detection.
 */
enum class GenericGestureType {
    CLICK,
    HOLD,
    SWIPE,
    CIRCLE,
    BOOMERANG,
    ;
}

