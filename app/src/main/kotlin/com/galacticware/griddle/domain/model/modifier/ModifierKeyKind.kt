package com.galacticware.griddle.domain.model.modifier

import android.view.KeyEvent
import androidx.compose.ui.graphics.Color
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.UNSHIFTED
import com.galacticware.griddle.domain.view.colorization.Hue


/**
 * Enumeration of the types of modifier keys accepted in Griddle.
 */
enum class ModifierKeyKind(
    val appSymbol: AppSymbol,
    val prettyName: String,
) {
    CONTROL(AppSymbol.CONTROL, "Control"),
    SHIFT(AppSymbol.SHIFT, "Shift"),
    ALT(AppSymbol.ALT, "Alt"),
    ;
    val intValue: Int by lazy {
        when(this) {
            SHIFT -> KeyEvent.META_SHIFT_MASK
            CONTROL -> KeyEvent.META_CTRL_MASK
            ALT -> KeyEvent.META_ALT_MASK
        }
    }

    fun theme(modifierAction: ModifierAction): ModifierThemeSet = run {
        val noneShiftTheme = ModifierTheme(
            primaryTextColor = Color.White,
            secondaryTextColor = Color.Black,
            primaryBorderColor = Color.Transparent,
            secondaryBorderColor = Color.Transparent,
            text = appSymbol.value,
            keyCode = null,
            primaryBackgroundColor = Color.Transparent,
            secondaryBackgroundColor = Color.Transparent,
        )
        val onceShiftTheme = noneShiftTheme.withText(AppSymbol.SHIFT.value)
            .withTextColor(Color.Yellow)
        when (this) {
            SHIFT -> {
                ModifierThemeSet(
                    modifierKeyKind = this,
                    none = noneShiftTheme,
                    once = onceShiftTheme,
                    repeat = onceShiftTheme.withTextColor(Color.Red)
                ).let {
                    val modifierThemeSet = if (modifierAction == ModifierAction.RELEASE) {
                        it.withTextTriple("", UNSHIFTED.value, UNSHIFTED.value)
                            .withTextColorSet(Color.Transparent, Color.White)
                    } else it
                    modifierThemeSet
                }
            }
            CONTROL, ALT -> {
                val noneControlTheme = ModifierTheme(
                    primaryTextColor = Color.Black,
                    secondaryTextColor = Color.White,
                    primaryBorderColor = Color.Transparent,
                    secondaryBorderColor = Color.Transparent,
                    text = AppSymbol.CONTROL.value,
                    keyCode = null,
                    primaryBackgroundColor = Hue.MEOK_LIGHT_GRAY.hex,
                    secondaryBackgroundColor = Hue.DARK_MAGENTA.hex
                )
                val onceControlTheme = noneShiftTheme
                    .withButtonBackgroundColor(Color.Yellow)
                    .withTextColor(Color.Black)
                val theme = ModifierThemeSet(
                    modifierKeyKind = this,
                    none = noneControlTheme,
                    once = onceControlTheme,
                    repeat = onceControlTheme.withButtonBackgroundColor(Color.Red)
                )
                when(this) {
                    CONTROL -> theme
                    ALT -> theme.withTextTriple("ALT")
                    else -> throw IllegalStateException("Not control or alt?")
                }
            }
        }
    }


    override fun toString(): String = prettyName

    companion object {
        val symbols = entries.map { it.appSymbol }.toSet()
    }
}
