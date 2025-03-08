package com.galacticware.griddle.domain.model.appsymbol

import android.content.Context
import android.view.KeyEvent
import com.galacticware.griddle.R
import com.galacticware.griddle.domain.model.button.SettingsValueProvider
import com.galacticware.griddle.domain.model.button.settingsDisplay
import com.galacticware.griddle.domain.model.geometry.CartesianAxis
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues
import com.galacticware.griddle.domain.model.usercontolled.VibrationChoice
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.model.util.triple
import com.galacticware.griddle.domain.model.usercontolled.GestureTracingChoice
import com.galacticware.griddle.domain.model.usercontolled.TurboModeChoice

/**
 * Enumeration of all the boxes in the user settings popup screen which require a dynamic value to
 * be displayed.
 */
enum class SettingsValue(val provider: SettingsValueProvider) {
    VIBRATION(settingsDisplay {
        UserDefinedValues.current.userVibration.let {
            if (it.toggledChoice == VibrationChoice.OFF) "Off" else "${it.amplitude}"
        }
    }
    ),
    MIN_DRAG_LENGTH(settingsDisplay {
        "${UserDefinedValues.current.minimumDragLength}"
    }
    ),
    GESTURE_TRACING(settingsDisplay {
        "${
            if (UserDefinedValues.current.isGestureTracingEnabled == GestureTracingChoice.ON)
                "On" else "Off"
        }\n(click\nto\ntoggle)"
    }
    ),
    TURBO_MODE(settingsDisplay {
        "${
            if (UserDefinedValues.current.turboModeChoice == TurboModeChoice.ON)
                "On" else "Off"
        }\n(click\nto\ntoggle)"
    }
    )
}

/**
 *  This Enumeration helps us "fill in the blanks" on what text to use when binding keys that are
 *  not associated with an ascii character representation.
 *  It holds all symbols used to represent various non-ascii keys on the board. Defined in this enum
 *  class are symbols and accompanying text for the following scenarios:
 *
 * - Special functions of the Griddle Application, such as the symbol for cycling through menus.
 * - Labels for Settings are defined here, in the [AppSymbol.textTriple], which often take up a whole box next to a button
 * that has some functionality
 * - Remapped symbols for keycodes or keyboard shortcuts with no visual single-char ASCII representation
 */
enum class AppSymbol(
    val value: String = "",
    val textTriple: Triple<String, String, String> = Triple("", "", ""),
    val keyEventMasks: List<Int> = listOf(),
    val icon: Int? = null,
    val provider: SettingsValueProvider? = null,
    val uiActionClause: String = "that operation",
    val mirrorAxis: CartesianAxis? = null,
    val uiLabel: String = "",
) {
    NBSP(" ", uiActionClause = "using the non-breaking space"),
    /**
     * Maybe deprecated
     */
    BACKSPACE_SPAMMING_SETTINGS_LAYER("", triple("Backspace\nSpamming"), uiActionClause = "setting the backspace spamming"),
    CHANGE_COLORS_LAYER("🎨", triple("Change\nColors"), uiActionClause = "changing the colors layer"),
    ADD_REMOVE_LAYER("±", triple("Add or\nRemove\nLayer"), uiActionClause = "adding or removing the layer"),
    CYCLE_EMOJIS_FORWARD("\uD83D\uDD3C", uiActionClause = "cycling the emojis forward"),
    CYCLE_EMOJIS_REVERSE("\uD83D\uDD3D", uiActionClause = "cycling the emojis reverse"),
    CYCLE_CLIPBOARD_ENTRIES_PAGE_FORWARD("\uD83D\uDD3D", uiActionClause = "cycling the clipboard entries page forward"),
    MICROPHONE_CHAR("\uD83C\uDF99", uiActionClause = "using the microphone"),
    GRIDDLE("\uD83D\uDCF1", uiActionClause = "using the griddle symbol"),
    CYCLE_CLIPBOARD_ENTRIES_PAGE_REVERSE("\uD83D\uDD3D", uiActionClause = "cycling the clipboard entries page reverse"),
    NEW_LINE("⏎", uiActionClause = "pressing the enter"),
    DOTTED_CROSS_CHAR("⁜", uiActionClause = "using the dotted cross"),
    ENCLOSING_SQUARE("\u2460", uiActionClause = "using the enclosing square"),

    /**
     * Layer switches
     */
    ALPHA_LAYER("ABC", uiActionClause = "toggling the alpha layer"),
    USER_DEFINED_LAYER("\uD83D\uDC64", uiActionClause = "using the user defined layer"),
    UNIFIED_ALPHA_NUMERIC_LAYER("AZ09", uiActionClause = "toggling the alphanumeric unified layer"),
    NUMPAD_LAYER("NUMPAD", triple("NUMPAD"), uiActionClause = "toggling the numpad layer"),
    NUMERIC_LAYER("123", triple("123"), uiActionClause = "toggling the number layer"),
    FUNCTION_LAYER("\u2131", triple("\u2131"), uiActionClause = "toggling the function layer"),
    SINGLE_DESIGNER_BUTTON_LAYER("🔲", triple("Single\nDesigner\nButton"), uiActionClause = "using the single designer button layer"),

    /**
     * Screen switches
     */
    SWITCH_NESTED_SCREEN("SWITCH_NESTED_SCREEN"),
    LANGUAGE_PREFERENCES("🌐", triple("Language\nPreferences"), uiActionClause = "setting the language preferences"),
    TOGGLE_SETTINGS("⚙", uiActionClause = "toggling the settings screen"),
    TOGGLE_CLIPBOARD("", triple("\uD83D\uDCCB"), uiActionClause = "toggling the clipboard layer"),
    TEXT_REPLACEMENT_EDITOR("✎", triple("✎"), uiActionClause = "editing the textReplacements"),
    BUILD_YOUR_OWN_KEYBOARD("\uD83C\uDD30", triple("\uD83C\uDD30"), uiActionClause = "remapping keys on the keyboard"),
    AUTO_FIXERS("Auto\nFixers", triple("F̼I̼X̼"), uiActionClause = "opening the auto fixers screen"),
    GLOBAL_SETTINGS("⚙", uiActionClause = "opening the settings"),
    EMOJI("\uD83D\uDE00", uiActionClause = "using the emoji"),

    /**
     * Modifiers
     */
    CONTROL("CTL"/*, icon = R.drawable.control_symbol*/, uiActionClause = "using the control"),
    ALT("ALT", uiActionClause = "using the alt"),
    SHIFT("▲", uiActionClause = "shifting"),
    UNSHIFTED("▼", uiActionClause = "unshifting"),
    CAPSLOCKED("⇪", uiActionClause = "caps locking"),

    /**
     * User settings
     */
    USER_CHANGEABLE_SETTINGS("📳", triple("User\ndefined\nsettings"), uiActionClause = "opening the user settings screen"),
    DECREMENT("-", uiActionClause = "decreasing"),
    INCREMENT("+", uiActionClause = "increasing"),
    SPEED_INCREASE("⇧", uiActionClause = "increasing the speed"),
    SPEED_DECREASE("⇩", uiActionClause = "decreasing the speed"),
    VIBRATION_LABEL("Vibration", triple("Vibration"), uiActionClause = "setting the vibration"),
    CURRENT_VIBRATION_INTENSITY_DISPLAY(provider = SettingsValue.VIBRATION.provider, uiActionClause = "displaying the current vibration INTENSITY"),
    MINIMUM_DRAG_LENGTH_LABEL("Minimum Drag Length", triple("Minimum\nDrag\nLength"), uiActionClause = "setting the minimum drag length"),
    DECREASE_MINIMUM_DRAG_LENGTH("🔉", uiActionClause = "decreasing the minimum drag length"),
    CURRENT_MINIMUM_DRAG_LENGTH_DISPLAY(provider = SettingsValue.MIN_DRAG_LENGTH.provider, uiActionClause = "displaying the current minimum drag length"),
    INCREASE_MINIMUM_DRAG_LENGTH("🔊", uiActionClause = "increasing the minimum drag length"),
    CURRENT_TRACING_ENABLED_LABEL("Gesture\nTracing", uiActionClause = "enabling the gesture tracing"),
    CURRENT_TRACING_ENABLED_DISPLAY(provider = SettingsValue.GESTURE_TRACING.provider, uiActionClause = "displaying the current gesture tracing choice"),
    CURRENT_TURBO_MODE_ENABLED_LABEL("Turbo\nmode", uiActionClause = "toggling turbo mode"),
    IS_TURBO_MODE_ENABLED_DISPLAY(provider = SettingsValue.TURBO_MODE.provider, uiActionClause = "displaying the current turbo mode choice"),

    /**
     * Special key symbols which do not have a single-character ascii representation.
     */
    SPACE_CHAR(" ", uiActionClause = "using the space", uiLabel = "Space"),
    BACKSPACE_NO_IMAGE("{BACKSPACE}", uiActionClause = "backspacing (no image)", uiLabel = "Backspace (no image)"),
    BACKSPACE("⌫", uiActionClause = "backspacing", uiLabel = "Backspace"),
    GO("GO", uiActionClause = "pressing the go button", uiLabel = "Enter (Go)"),
    TAB_RIGHT("⇥", uiActionClause = "tabbing to the right", uiLabel = "Tab right"),
    DELETE("⌦", uiActionClause = "deleting", uiLabel = "Delete"),
    UP_ARROW("↑", uiActionClause = "moving up", uiLabel = "Up arrow"),
    DOWN_ARROW("↓", uiActionClause = "moving down", uiLabel = "Down arrow"),
    RIGHT_ARROW("⇾", uiActionClause = "moving to the right", uiLabel = "Right arrow"),
    LEFT_ARROW("⇽", uiActionClause = "moving to the left", uiLabel = "Left arrow"),
    MOVE_PGUP("PGUP", uiActionClause = "moving the page up", uiLabel = "Page up"),
    MOVE_PGDN("PGDN", uiActionClause = "moving the page down", uiLabel = "Page down"),
    TAB_LEFT("⇤", uiActionClause = "tabbing to the left", uiLabel = "Tab"),
    MOVE_HOME("HOME", uiActionClause = "moving to the home", uiLabel = "Home"),
    MOVE_END("END$NBSP", uiActionClause = "moving to the end", uiLabel = "End"),
    F1_SYMBOL(" F1", uiActionClause = "using the f1", uiLabel = "F1"),
    F2_SYMBOL(" F2", uiActionClause = "using the f2", uiLabel = "F2"),
    F3_SYMBOL(" F3", uiActionClause = "using the f3", uiLabel = "F3"),
    F4_SYMBOL(" F4", uiActionClause = "using the f4", uiLabel = "F4"),
    F5_SYMBOL(" F5", uiActionClause = "using the f5", uiLabel = "F5"),
    F6_SYMBOL(" F6", uiActionClause = "using the f6", uiLabel = "F6"),
    F7_SYMBOL(" F7", uiActionClause = "using the f7", uiLabel = "F7"),
    F8_SYMBOL(" F8", uiActionClause = "using the f8", uiLabel = "F8"),
    F9_SYMBOL(" F9", uiActionClause = "using the f9", uiLabel = "F9"),
    F10_SYMBOL("F10", uiActionClause = "using the f10", uiLabel = "F10"),
    F11_SYMBOL("F11", uiActionClause = "using the f11", uiLabel = "F11"),
    F12_SYMBOL("F12", uiActionClause = "using the f12", uiLabel = "F12"),
    COPY("⧉", keyEventMasks = listOf(KeyEvent.META_CTRL_ON), uiActionClause = "copying", uiLabel = "Copy"),
    CUT("✂", keyEventMasks = listOf(KeyEvent.META_CTRL_ON), uiActionClause = "cutting", uiLabel = "Cut"),
    PASTE("≡", keyEventMasks = listOf(KeyEvent.META_CTRL_ON), uiActionClause = "pasting", uiLabel = "Paste"),
    SELECT_ALL_TEXT("ₐₗₗ", uiActionClause = "selecting all the text", uiLabel = "Select all"),
    UNDO("⎌", keyEventMasks = listOf(KeyEvent.META_CTRL_ON), uiActionClause = "undoing", uiLabel = "Undo"),
    REDO("⎌", keyEventMasks = listOf(KeyEvent.META_CTRL_ON), uiActionClause = "redoing", mirrorAxis = CartesianAxis.Y, uiLabel = "Redo"),
    DELETE_WORD_LEFT("\uD83E\uDC44", uiActionClause = "deleting the word left", uiLabel = "Delete word right (ctrl + Delete)"),
    DELETE_WORD_RIGHT("\uD83E\uDC46", uiActionClause = "deleting the word right", uiLabel = "Delete word left (ctrl + Backspace)"),
    MOVE_WORD_LEFT("↶", uiActionClause = "moving the word left", uiLabel = "Move word left (ctrl + left)"),
    MOVE_WORD_RIGHT("↷", uiActionClause = "moving the word right", uiLabel = "Move word right (ctrl + right)"),

    /**
     * Clipboard
     */
    SHOW_CLIPBOARD("📋", uiActionClause = "showing the clipboard"),
    SET_CLIPBOARD_COPY_CANDIDATE("SET_CLIPBOARD_COPY_CANDIDATE", uiActionClause = "setting the clipboard copy candidate"),
    OPEN_CLIPBOARD("\uD83D\uDCCB", uiActionClause = "opening the clipboard"),

    /**
     * Board sizing and movement
     */
    RESIZE_BOARD("⇱↕⇲", triple("Resize\nBoard"), uiActionClause = "resizing the board"),
    FINISH_RESIZING("OK", uiActionClause = "finishing the resizing"),

    /**
     * Other
     */
    CYCLE_ACCENTED_CHARS("\uD83C\uDD32 ", uiActionClause = "cycling the accented characters"),
    SWAP_HANDEDNESS("↺", uiActionClause = "swapping the handedness"),
    REPEAT("🔁", icon = R.drawable.repeat, uiActionClause = "repeating"),
    TRASH_BIN("\uD83D\uDDD1", uiActionClause = "using the trash bin"),
    LOGS("LOGS", uiActionClause = "viewing the logs"),
    SEARCH("🔍", uiActionClause = "searching"),
    GO_BACK_ENGLISH(/*🔙*/"↩", triple("↩"), uiActionClause = "going back"),
    CHOOSE_DIFFERENT_INPUT_METHOD("Choose\ndifferent\ninput\nmethod\n🔠", triple("🔠"), uiActionClause = "choosing a different input method"),
    WORD_PREDICTION("❌", triple("Open Word Prediction")),
    SPEECH_TO_TEXT("\uD83D\uDDE8", uiActionClause = "Converting speech to text"),
    HIDE_KEYBOARD("HIDE", uiActionClause = "Hiding the keyboard"),
    ;

    val currentDisplayText: String
        get() = provider?.provideValue() ?: textTriple.first.ifEmpty { value }
    companion object {
        val knownSettingsItemSizes = mutableMapOf<AppSymbol, Float>()
        fun initializeSettingsItemSizes(context: Context) {
            entries.forEach {
                knownSettingsItemSizes[it] = PreferencesHelper.getAppSymbolSize(context, it)
            }
        }

        fun getKnownSettingsItemSizeFor(context: Context, symbol: AppSymbol) =
            knownSettingsItemSizes[symbol]
                ?: PreferencesHelper.getAppSymbolSize(context, symbol)

        /**
         * Any AppSymbol that stands for a keycode or keyboard shortcut will be here.
         * This is to support keycodes that have no standard, single-character ASCII representation.
         * TODO, we should use a nerd font that has ligatures
         *  for all the keycodes we use, so that each of these will be just one character
         *  in length, which would make sizing of the text simpler and more consistent.
         */
        val specialKeySymbols =
            mapOf(
                SPACE_CHAR to KeyEvent.KEYCODE_SPACE,
                BACKSPACE to KeyEvent.KEYCODE_DEL,
                BACKSPACE_NO_IMAGE to KeyEvent.KEYCODE_DEL,
                GO to KeyEvent.KEYCODE_ENTER,
                TAB_RIGHT to KeyEvent.KEYCODE_TAB,
                DELETE to KeyEvent.KEYCODE_FORWARD_DEL,
                UP_ARROW to KeyEvent.KEYCODE_DPAD_UP,
                DOWN_ARROW to KeyEvent.KEYCODE_DPAD_DOWN,
                LEFT_ARROW to KeyEvent.KEYCODE_DPAD_LEFT,
                RIGHT_ARROW to KeyEvent.KEYCODE_DPAD_RIGHT,
                MOVE_PGUP to KeyEvent.KEYCODE_PAGE_UP,
                MOVE_PGDN to KeyEvent.KEYCODE_PAGE_DOWN,
                MOVE_END to KeyEvent.KEYCODE_MOVE_END,
                MOVE_HOME to KeyEvent.KEYCODE_HOME,
                F1_SYMBOL to KeyEvent.KEYCODE_F1,
                F2_SYMBOL to KeyEvent.KEYCODE_F2,
                F3_SYMBOL to KeyEvent.KEYCODE_F3,
                F4_SYMBOL to KeyEvent.KEYCODE_F4,
                F4_SYMBOL to KeyEvent.KEYCODE_F4,
                F5_SYMBOL to KeyEvent.KEYCODE_F5,
                F6_SYMBOL to KeyEvent.KEYCODE_F6,
                F7_SYMBOL to KeyEvent.KEYCODE_F7,
                F8_SYMBOL to KeyEvent.KEYCODE_F8,
                F9_SYMBOL to KeyEvent.KEYCODE_F9,
                F10_SYMBOL to KeyEvent.KEYCODE_F10,
                F11_SYMBOL to KeyEvent.KEYCODE_F11,
                F12_SYMBOL to KeyEvent.KEYCODE_F12,
                COPY to KeyEvent.KEYCODE_C,
                CUT to KeyEvent.KEYCODE_X,
                PASTE to KeyEvent.KEYCODE_V,
                UNDO to KeyEvent.KEYCODE_Z,
                REDO to KeyEvent.KEYCODE_Y,
                DELETE_WORD_LEFT to KeyEvent.KEYCODE_DEL,
                DELETE_WORD_RIGHT to KeyEvent.KEYCODE_FORWARD_DEL,
                MOVE_WORD_LEFT to KeyEvent.KEYCODE_DPAD_LEFT,
                MOVE_WORD_RIGHT to KeyEvent.KEYCODE_DPAD_RIGHT,
            )
    }
}
