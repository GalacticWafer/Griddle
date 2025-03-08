package com.galacticware.griddle.domain.model.layer

import android.content.Context
import androidx.compose.ui.unit.IntSize
import com.galacticware.griddle.domain.model.button.GestureButtonBuilder
import com.galacticware.griddle.domain.model.keyboard.KeyboardHandedness
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.keyboard.system.layerkind.AbstractKeyboardLayer

fun CreateLayer(
    context: Context,
    name: String,
    builders: MutableSet<GestureButtonBuilder>,
    keyboardHandedness: KeyboardHandedness,
    defaultButtonSize: IntSize,
    isPrimary: Boolean,
    layerKind: LayerKind,
    languageTag: LanguageTag?,
) : AbstractKeyboardLayer {
    return object: AbstractKeyboardLayer(
        context = context,
        gestureButtonBuilders = builders,
        name = name,
        keyboardHandedness = keyboardHandedness,
        defaultButtonSize = defaultButtonSize,
        isPrimary = isPrimary,
        layerKind = layerKind,
        languageTag = languageTag,
    ){}
}