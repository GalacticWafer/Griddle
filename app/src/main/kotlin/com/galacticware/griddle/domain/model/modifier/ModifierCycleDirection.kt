package com.galacticware.griddle.domain.model.modifier

/**
 * The direction in which a modifier cycle should proceed.
 */

enum class ModifierCycleDirection(val isReversed: Boolean) {
    FORWARD(false),
    REVERSE(true),
}