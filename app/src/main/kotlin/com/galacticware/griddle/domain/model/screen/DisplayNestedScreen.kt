package com.galacticware.griddle.domain.model.screen

import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.geometry.BoardEdge

interface DisplayNestedScreen {
    @Composable
    fun Show()
    val displayNextToKeyboardEdge: BoardEdge?
}