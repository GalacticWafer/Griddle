package com.galacticware.applicationsd

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.button.GestureButtonBuilder
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.view.colorization.Hue
import org.junit.Test
import org.junit.Assert.*

class GestureButtonTest {

    @Test
    fun testGestureButton() {
        val defaultModifierTheme = ModifierTheme(
            primaryTextColor = Hue.MEOK_DEFAULT_YELLOW.hex,
            primaryBackgroundColor = Hue.MEOK_DARK_GRAY.hex,
            primaryBorderColor = Hue.MEOK_LIGHT_GRAY.hex,
            secondaryTextColor = Color.White,
            secondaryBackgroundColor = Hue.MEOK_LIGHT_GRAY.hex,
        )
        // Arrange
        val rowStart = 1
        val colStart = 2
        val rowSpan = 3
        val colSpan = 4
        val gestureSet = mutableSetOf<Gesture>()
        val widthRuler = 10
        val heightRuler = 20
        val modifierTheme = defaultModifierTheme
        val settingsValueProvider = null
        val isPeripheral = false

        val originalBuilder = GestureButtonBuilder.gestureButton(
            rowStart,
            colStart,
            rowSpan,
            colSpan,
            gestureSet,
            size = IntSize(widthRuler, heightRuler),
            modifierTheme,
            settingsValueProvider,
            isPeripheral
        )

        // Act
        val griddleGestureButton = originalBuilder()
        val newBuilder = griddleGestureButton.builder

        // Assert
        assertEquals(originalBuilder.size.width, newBuilder.size.width)
        assertEquals(originalBuilder.size.height, newBuilder.size.height)
    }
}
