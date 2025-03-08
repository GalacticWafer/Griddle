package com.galacticware.griddle.domain.model.input

enum class TextSelectionAnchor {
    NONE,
    LEFT,
    RIGHT;

    fun indices(assumedStart: Int, assumedEnd: Int): Pair<Int, Int> {
        return when (this) {
            NONE, LEFT -> Pair(assumedStart, assumedStart)
            RIGHT -> Pair(assumedEnd, assumedStart)
        }
    }

    companion object {
        var currentPosition: Int? = null
    }
}