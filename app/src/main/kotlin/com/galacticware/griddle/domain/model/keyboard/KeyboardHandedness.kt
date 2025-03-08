package com.galacticware.griddle.domain.model.keyboard

import kotlinx.serialization.Serializable

@Serializable
data class KeyboardHandedness(
    val hasHandedness: Boolean,
    val pivotColumn: Int,
)