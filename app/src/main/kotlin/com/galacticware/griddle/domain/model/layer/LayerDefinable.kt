package com.galacticware.griddle.domain.model.layer

import android.content.Context
import androidx.compose.ui.unit.dp
import com.galacticware.griddle.domain.model.keyboard.KeyboardHandedness
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.button.GestureButtonBuilder
import com.galacticware.griddle.domain.model.keyboard.KeyboardOffsetAndSize
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.keyboard.system.layerkind.AbstractKeyboardLayer
import com.galacticware.griddle.domain.model.keyboard.system.layerkind.LayerModel

/**
 * Interface for all [AbstractKeyboardLayer]s such that they are all provide a singleton instance of
 * their class, and a way to load their Set<GestureButton> for the corresponding layer.
 */
interface LayerDefinable {

    /**
     * Load the set of [GestureButton] objects to build a layer.
     */
    fun loadButtons(
        replacementButtons: Map<Pair<Int, Int>, Pair<GestureButton, List<Pair<ModifierTheme, Gesture>>>>?
    ): Map<Pair<Int, Int>, Pair<GestureButton, List<Pair<ModifierTheme, Gesture>>>>

    val name: String
    val isPrimary: Boolean
    var defaultModifierTheme: ModifierTheme
    var secondaryModifierTheme: ModifierTheme
    var gestureButtonBuilders: MutableSet<GestureButtonBuilder>
    val layerKind: LayerKind
    val languageTag: LanguageTag?
    val keyboardHandedness: KeyboardHandedness
    var rowHeight: Int
    var colWidth: Int
    val colSpan: Int
    val rowSpan: Int
    val originalRowHeight: Int
    val originalColWidth: Int
    val minBoardHeight get() = 100.dp
    val maxBoardHeight get() = 700.dp
    val minRowHeight get() = 50
    val maxRowHeight get() = 100
    val minColWidth get() = 70
    val maxColWidth get() = run {
        val originalAspectRatio = originalColWidth / originalRowHeight
        return@run (originalAspectRatio * rowHeight).let {
            if (it < minColWidth) minColWidth else it
        }
    }
    var offsetX: Float

    fun minBoardWidth(context: Context) = (context.resources.displayMetrics.widthPixels * .33f)
    fun maxBoardWidth(context: Context) = context.resources.displayMetrics.widthPixels

    fun resizeToFitScreen()
    fun saveBoardPositionAndSize(value: KeyboardOffsetAndSize)
    val model: LayerModel
}