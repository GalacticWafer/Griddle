package com.galacticware.griddle.domain.model.geometry

import com.galacticware.griddle.domain.model.button.GestureButton

enum class BoardEdge {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT;

    companion object {
        fun edgesForButton(gestureButton: GestureButton, boardRowSpan: Int, boardColumnSpan: Int): Set<BoardEdge> {
            val edges = mutableSetOf<BoardEdge>()
            if (gestureButton.rowStart == 0) edges.add(TOP)
            if(gestureButton.colStart == 0) edges.add(LEFT)
            if(gestureButton.colStart + gestureButton.rowSpan == boardRowSpan) edges.add(RIGHT)
            if(gestureButton.rowStart + gestureButton.colSpan == boardColumnSpan) edges.add(BOTTOM)
            return edges
        }
    }
}