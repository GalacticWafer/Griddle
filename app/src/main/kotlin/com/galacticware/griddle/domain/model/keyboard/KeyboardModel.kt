package com.galacticware.griddle.domain.model.keyboard

import android.content.Context
import com.galacticware.griddle.domain.model.keyboard.system.layerkind.LayerModel
import kotlinx.serialization.Serializable

@Serializable
data class KeyboardModel(
    val layerModels: List<LayerModel>,
    val keyboardKind: KeyboardKind = KeyboardKind.DEFAULT,
    val name: String,
) {
    fun toKeyboard(context: Context, keyboardName: String = name) = Keyboard(
        layers = layerModels.map { it.toLayer(context) }.toMutableSet(),
        keyboardKind = keyboardKind,
        name = keyboardName,
    )
}