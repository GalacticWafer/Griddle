package com.galacticware.griddle.domain.model.modifier

/**
 * Keeps track of the state of a [ModifierKeyKind] to support none, one-shot, and held (i.e., [REPEAT]) modifiers.
 */
enum class ModifierKeyState {
    NONE,
    ONCE,
    REPEAT,;
    companion object {
        val nextModifier = mapOf(
            NONE to NONE,
            ONCE to NONE,
            REPEAT to REPEAT,
        )
    }
    fun cancelOneShotModifier(): ModifierKeyState {
        return nextModifier[this]!!
    }
    fun next() = entries[(ordinal + 1) % entries.size]
}