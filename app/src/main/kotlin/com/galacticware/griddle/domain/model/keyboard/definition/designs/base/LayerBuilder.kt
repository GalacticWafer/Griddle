package com.galacticware.griddle.domain.model.keyboard.definition.designs.base

import android.content.Context
import androidx.compose.ui.unit.IntSize
import com.galacticware.griddle.domain.model.button.GestureButtonBuilder
import com.galacticware.griddle.domain.model.keyboard.KeyboardHandedness
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.layer.LayerDefinable
import com.galacticware.griddle.domain.model.layer.LayerKind

interface LayerBuilder {
    val name: String
    val languageTag: LanguageTag?
    val layerKind: LayerKind
    val isPrimary: Boolean
    val keyboardHandedness: KeyboardHandedness
    val defalultSize: IntSize
    fun buttonBuilders(): MutableSet<GestureButtonBuilder>
    fun build(context: Context): LayerDefinable
}