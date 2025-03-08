package com.galacticware.griddle.domain.view.colorization

import androidx.compose.ui.graphics.Color
import com.galacticware.griddle.domain.model.modifier.ModifierTheme

/**
 * Enum for common application colors
 */
enum class Hue(val hex: Color) {
    BERGUNDY(Color(0xAAC04500)),
    PINK(Color(0xFFEE00FF)),
    DARK_MAGENTA(Color(0xFF85019F)),
    PURPLE(Color(0xFF4938AF)),
    BLUE(Color(0xFF0033FF)),
    CYAN(Color(0xFF2BF7F4)),
    GREEN(Color(0xBF2AF93A)),
    HIGHLIGHTER_GREEN(Color(0xFF14B31D)),
    YELLOW(Color(0xFFFFF00F)),
    GRELLOW(Color(0xF7C6F49F)),
    TAN(Color(0xF3B37F78)),
    ORANGE(Color(0xFFD97F00)),
    MEOK_LIGHT_GRAY(Color(0xFFAAAAAA)),
    MEOK_DARK_GRAY(Color(0xFF333A48)),
    MEOK_DEFAULT_YELLOW(Color(0xFFD4AC04)),
    ROARNGE(Color(0xFFFF3A00)),
    purple200(Color(0xFFBB86FC)),
    purple500(Color(0xFF6200EE)),
    purple700(Color(0xFF3700B3)),
    teal200(Color(0xFF03DAC5)),

            ;

    companion object {

        val grayBackground = ModifierTheme(
            primaryTextColor = Color(0xFF000000),
            primaryBackgroundColor = Color(0xFFAAAAAA),
            primaryBorderColor = Color.Transparent,
        )
        val yellowBackground = grayBackground.withButtonBackgroundColor(Color(0xFFFFFF00))
        val redBackground = grayBackground.withButtonBackgroundColor(Color(0xFFFF0000))
    }
}
