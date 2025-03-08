package com.galacticware.griddle.domain.model.keyboard

import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

data class KeyboardOffsetAndSize(
    val offsetX: Float,
    val width: Float,
    val height: Float,
) {
    val offsetY: Float = 0f
    val intOffset get() = IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
}