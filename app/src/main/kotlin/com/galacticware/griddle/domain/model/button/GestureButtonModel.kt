package com.galacticware.griddle.domain.model.button

import androidx.compose.ui.unit.IntSize
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.gesture.GestureModel
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.appsymbol.SettingsValue
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import kotlinx.serialization.Serializable

/**
 * Builder for GestureButton objects. Returns a function that can be invoked to create multiple
 * instances of the gesture button.
 */
@Serializable
class GestureButtonModel(
    val gridPosition: GridPosition,
    val gestureModelSet: Map<GestureType, GestureModel>,
    @Serializable(with = IntSizeSerializer::class) val size: IntSize,
    var modifierTheme: ModifierTheme,
    val settingsValueProvider: SettingsValue?,
    val isPeripheral: Boolean,
) {
    fun toGestureButtonBuilder() = GestureButtonBuilder(
        gridPosition,
        gestureModelSet.map { it.value.toGesture(it.key) }.toMutableSet(),
        size,
        modifierTheme,
        settingsValueProvider?.provider,
        isPeripheral,
    )
}
