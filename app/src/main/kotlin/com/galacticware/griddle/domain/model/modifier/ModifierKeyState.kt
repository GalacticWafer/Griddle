package com.galacticware.griddle.domain.model.modifier

/**
 * Keeps track of the state of a [ModifierKeyKind] to support none, one-shot, and held (i.e., [ON]) modifiers.
 */
enum class ModifierKeyState {
    OFF,
    ONE_SHOT,
    ON,;
    companion object {
        val nextModifier = mapOf(
            OFF to OFF,
            ONE_SHOT to OFF,
            ON to ON,
        )
    }
    fun cancelOneShotModifier(): ModifierKeyState {
        return nextModifier[this]!!
    }
    fun next() = entries[(ordinal + 1) % entries.size]
}