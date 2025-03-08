package com.galacticware.griddle.domain.model.keyboard.system.layerkind

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import com.galacticware.griddle.domain.model.button.GestureButtonModel
import com.galacticware.griddle.domain.model.button.IntSizeSerializer
import com.galacticware.griddle.domain.model.keyboard.KeyboardHandedness
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.modifier.ColorSerializer
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.modifier.TextUnitSerializer
import kotlinx.serialization.Serializable

@Serializable
data class LayerModel(
    var gestureButtonBuilderModels: Map<String, GestureButtonModel>,
    @Serializable(with = ColorSerializer::class)
    var borderColor: Color,
    @Serializable(with = ColorSerializer::class)
    var backgroundColor: Color,
    @Serializable(with = ColorSerializer::class)
    var textColor: Color,
    @Serializable(with = TextUnitSerializer::class)
    var fontSize: TextUnit,
    val keyboardHandedness: KeyboardHandedness,
    @Serializable(with = IntSizeSerializer::class)
    val defaultButtonSize: IntSize,
    var secondaryModifierTheme: ModifierTheme,
    val isPrimary: Boolean,
    val name: String,
    val layerKind: LayerKind,
    val languageTag: LanguageTag?,
) {
    fun toLayer(context: Context) = object : AbstractKeyboardLayer(
        context,
        gestureButtonBuilders = gestureButtonBuilderModels.values.map { it.toGestureButtonBuilder() }.toMutableSet(),
        borderColor,
        backgroundColor,
        textColor,
        fontSize,
        keyboardHandedness,
        defaultButtonSize,
        secondaryModifierTheme,
        isPrimary,
        name,
        layerKind,
        languageTag,
    ){}
}
