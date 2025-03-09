package com.galacticware.griddle.domain.model.modifier

import androidx.compose.ui.graphics.Color
/*import androidx.room.Entity
import androidx.room.PrimaryKey*/
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_PRIMARY_THEME
import com.galacticware.griddle.domain.view.colorization.Hue
import kotlinx.serialization.Serializable

/**
 * This class provides static methods to simplify the building of [ModifierThemeSet] objects.
 * The [ModifierThemeSet] has three [ModifierTheme]s corresponding to what the key should look like
 * depending on the current [ModifierKeyState] of the specified [modifierKeyKind].
 */
@Serializable
data class ModifierThemeSet(
    val modifierKeyKind: ModifierKeyKind,
    val none: ModifierTheme,
    val once: ModifierTheme = none,
    val repeat: ModifierTheme = once,
) {

    fun currentText() = currentText(this, Triple(none.text?:"", once.text?:"", repeat.text?:""))
    companion object {
        /**
         * A triple of Transparent [ModifierTheme]s.
         */
        val BLANK: ModifierThemeSet = ModifierThemeSet(
            none = ModifierTheme.BLANK,
            once = ModifierTheme.BLANK,
            repeat = ModifierTheme.BLANK,
            modifierKeyKind = ModifierKeyKind.SHIFT,
        )

        /**
         * Apply the default theme set of gray, yellow, and red to the provided texts for [ModifierKeyState.OFF],
         * [ModifierKeyState.ONE_SHOT], and [ModifierKeyState.ON] states respectively.
         * For convenience, creators have to provide text only for the none state, or for both
         * the none and once states, or for all three states. In other words, you have to provide text
         * for all the states lower than the highest ordinal [ModifierKeyState] you care about.
         */
        fun forModifierWithDefaultTheme(
            noneStateText: String,
            onceStateText: String = noneStateText.uppercase(),
            repeatStateText: String = onceStateText,
            kind: ModifierKeyKind,
        ) = ModifierThemeSet(
            none = Hue.grayBackground.withText(noneStateText),
            once = Hue.yellowBackground.withText(onceStateText),
            repeat = Hue.redBackground.withText(repeatStateText),
            modifierKeyKind = kind,
        )

        /**
         * Apply the [modifierTheme] thruple
         * NOTE: This method applies the the colorization of the default grid if none is provided.
         */
        fun allSameText(
            first: String,
            modifierTheme: ModifierTheme = DEFAULT_PRIMARY_THEME,
            kind: ModifierKeyKind = ModifierKeyKind.SHIFT
        ): ModifierThemeSet {
            val second = first.uppercase()
            return ModifierThemeSet(
                none = modifierTheme.withText(first),
                once = modifierTheme.withText(second),
                repeat = modifierTheme.withText(second),
                modifierKeyKind = kind,
            )
        }

        /**
         * Apply the provided theme for all [ModifierKeyState]s
         * For convenience, creators have to provide text only for the none state, or for both
         * the none and once states, or for all three states. In other words, you have to provide text
         * for all the states lower than the highest ordinal [ModifierKeyState] you care about.
         */
        fun allSameTheme(
            modifierTheme: ModifierTheme,
            noneStateText: String,
            onceStateText: String = noneStateText,
            repeatStateText: String = onceStateText,
            modifierKeyKind: ModifierKeyKind = ModifierKeyKind.SHIFT,
        ): ModifierThemeSet {
            return ModifierThemeSet(
                none = modifierTheme.withText(noneStateText),
                once = modifierTheme.withText(onceStateText),
                repeat = modifierTheme.withText(repeatStateText),
                modifierKeyKind = modifierKeyKind,
            )
        }

        fun currentText(
            modifierThemeSet: ModifierThemeSet,
            textTriple: Triple<String, String, String>
        ) = run {
            val state = when (modifierThemeSet.modifierKeyKind) {
                ModifierKeyKind.SHIFT -> Keyboard.shiftState
                ModifierKeyKind.CONTROL -> Keyboard.ctrlState
                ModifierKeyKind.ALT -> Keyboard.altState
            }
            when (state) {
                ModifierKeyState.OFF -> textTriple.first
                ModifierKeyState.ONE_SHOT -> textTriple.second
                ModifierKeyState.ON -> textTriple.third
            }.let { t->
               if (t.length == 1 && !t[0].isLetter() &&
                    Keyboard.shiftState != ModifierKeyState.OFF)
                    textTriple.first
                else
                    t
            }
        }
    }

    /**
     * Return a copy of this [ModifierThemeSet] with the overwritten [ModifierKeyState] theme as
     * specified by [modifierKeyState]
     */
    fun withNoneMetaStateTheme(
        newColorSet: ModifierTheme,
        modifierKeyState: ModifierKeyState
    ): ModifierThemeSet {
        val (griddleModifierKeyKind, noneColorSet, onceColorSet, repeatColorSet) = copy(none = newColorSet)
        return when (modifierKeyState) {
            ModifierKeyState.OFF -> ModifierThemeSet(griddleModifierKeyKind, newColorSet, onceColorSet, repeatColorSet)
            ModifierKeyState.ONE_SHOT -> ModifierThemeSet(griddleModifierKeyKind, noneColorSet, newColorSet, repeatColorSet)
            ModifierKeyState.ON -> ModifierThemeSet(griddleModifierKeyKind, noneColorSet, onceColorSet, newColorSet)
        }
    }

    /**
     * Return a copy of this [ModifierThemeSet] with the overwritten [ModifierKeyState.OFF] theme.
     */
    fun withTextTriple(symbol1: String, symbol2: String = symbol1, symbol3: String = symbol2): ModifierThemeSet {
        return copy(
            none = none.withText(symbol1),
            once = once.withText(symbol2),
            repeat = repeat.withText(symbol3),
        )
    }

    /**
     * Return a copy of this [ModifierThemeSet] with the overwritten [ModifierKeyState.OFF] theme.
     */
    fun withTextTriple(triple: Triple<String, String, String>): ModifierThemeSet {
        return copy(
            none = none.withText(triple.first),
            once = once.withText(triple.second),
            repeat = repeat.withText(triple.third),
        )
    }

    /**
     * Return a copy of this [ModifierThemeSet] with the overwritten [ModifierKeyState.OFF] theme.
     */
    fun withTransparentBackground(): ModifierThemeSet {
        return copy(
            none = none.withButtonBackgroundColor(Color.Transparent),
            once = once.withButtonBackgroundColor(Color.Transparent),
            repeat = repeat.withButtonBackgroundColor(Color.Transparent),
        )
    }

    /**
     * Save the text as the specified [ModifierKeyState]'s text.
     */
    fun updateText(newText: String, modifierKeyState: ModifierKeyState) {
        when (modifierKeyState) {
            ModifierKeyState.OFF -> none.text = newText
            ModifierKeyState.ONE_SHOT -> once.text = newText
            ModifierKeyState.ON -> repeat.text = newText
        }
    }

    fun withTextColorSet(
        noneColor: Color,
        onceColor: Color = noneColor,
        repeatColor: Color = onceColor,
    ): ModifierThemeSet {
        return copy(
            none = none.withTextColor(noneColor),
            once = once.withTextColor(onceColor),
            repeat = repeat.withTextColor(repeatColor),
        )
    }
    fun withTextColorSet(
        colors: Triple<Color, Color, Color>
    ): ModifierThemeSet {
        return copy(
            none = none.withTextColor(colors.first),
            once = once.withTextColor(colors.second),
            repeat = repeat.withTextColor(colors.third),
        )
    }

    fun withBackgroundColorSet(
        colors: Triple<Color, Color, Color>
    ): ModifierThemeSet {
        return copy(
            none = none.withButtonBackgroundColor(colors.first),
            once = once.withButtonBackgroundColor(colors.second),
            repeat = repeat.withButtonBackgroundColor(colors.third),
        )
    }
    fun withBackgroundColorSet(
        noneColor: Color,
        onceColor: Color = noneColor,
        repeatColor: Color = onceColor,
    ): ModifierThemeSet {
        return copy(
            none = none.withButtonBackgroundColor(noneColor),
            once = once.withButtonBackgroundColor(onceColor),
            repeat = repeat.withButtonBackgroundColor(repeatColor),
        )
    }

    fun withBorderColorSet(
        noneColor: Color,
        onceColor: Color = noneColor,
        repeatColor: Color = onceColor,
    ): ModifierThemeSet {
        return copy(
            none = none.withButtonBorderColor(noneColor),
            once = once.withButtonBorderColor(onceColor),
            repeat = repeat.withButtonBorderColor(repeatColor),
        )
    }

    fun currentTheme(): ModifierTheme {
        return when (Keyboard.shiftState) {
            ModifierKeyState.OFF -> none
            ModifierKeyState.ONE_SHOT -> once
            ModifierKeyState.ON -> repeat
        }
    }

    fun forModifier(modifierKeyKind: ModifierKeyKind): ModifierThemeSet {
        return copy(modifierKeyKind = modifierKeyKind)
    }

    val textTriple: Triple<String, String, String> get() = Triple(none.text?:"", once.text?:"", repeat.text?:"")
}

