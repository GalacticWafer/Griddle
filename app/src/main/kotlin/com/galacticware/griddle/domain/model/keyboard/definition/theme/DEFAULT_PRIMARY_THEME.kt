package com.galacticware.griddle.domain.model.keyboard.definition.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.view.colorization.Hue
val defaultTextColorTriple = Triple(Color.White, Hue.YELLOW.hex, Color.Red)
val unShiftTextColorTriple = Triple(Color.Transparent, Color.Transparent, Color.Red)
val modifierTextColorTriple = Triple(Color.Black, Color.Black, Color.Black)
val modifierBackgroundColorTriple = Triple(Color.Transparent, Color.Yellow, Color.Green)
val DEFAULT_PRIMARY_THEME = ModifierTheme(
    primaryTextColor = Hue.MEOK_DEFAULT_YELLOW.hex,
    primaryBackgroundColor = Hue.MEOK_DARK_GRAY.hex,
    primaryBorderColor = Hue.MEOK_LIGHT_GRAY.hex,
)
val DEFAULT_SECONDARY_THEME = ModifierTheme(
    primaryBorderColor = Hue.MEOK_LIGHT_GRAY.hex,
    primaryTextColor = Color.White,
    primaryBackgroundColor = Hue.YELLOW.hex,
)

val DEFAULT_FONT_SIZE = 23f
val DEFAULT_SIZE = IntSize(90, 70)
