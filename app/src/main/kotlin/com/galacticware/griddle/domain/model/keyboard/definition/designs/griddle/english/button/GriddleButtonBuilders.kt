package com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.button

import android.view.KeyEvent.KEYCODE_A
import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_FORWARD_DEL
import android.view.KeyEvent.KEYCODE_PAGE_DOWN
import android.view.KeyEvent.KEYCODE_PAGE_UP
import android.view.KeyEvent.KEYCODE_SPACE
import android.view.KeyEvent.KEYCODE_Y
import android.view.KeyEvent.KEYCODE_Z
import com.galacticware.griddle.domain.model.button.GestureButtonBuilder.Companion.gestureButton
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GestureType.BOOMERANG_DOWN
import com.galacticware.griddle.domain.model.gesture.GestureType.BOOMERANG_DOWN_LEFT
import com.galacticware.griddle.domain.model.gesture.GestureType.BOOMERANG_DOWN_RIGHT
import com.galacticware.griddle.domain.model.gesture.GestureType.BOOMERANG_LEFT
import com.galacticware.griddle.domain.model.gesture.GestureType.BOOMERANG_RIGHT
import com.galacticware.griddle.domain.model.gesture.GestureType.BOOMERANG_UP
import com.galacticware.griddle.domain.model.gesture.GestureType.BOOMERANG_UP_LEFT
import com.galacticware.griddle.domain.model.gesture.GestureType.BOOMERANG_UP_RIGHT
import com.galacticware.griddle.domain.model.gesture.GestureType.CIRCLE_ANTI_CLOCKWISE
import com.galacticware.griddle.domain.model.gesture.GestureType.CIRCLE_CLOCKWISE
import com.galacticware.griddle.domain.model.gesture.GestureType.CLICK
import com.galacticware.griddle.domain.model.gesture.GestureType.HOLD
import com.galacticware.griddle.domain.model.gesture.GestureType.SWIPE_DOWN
import com.galacticware.griddle.domain.model.gesture.GestureType.SWIPE_DOWN_LEFT
import com.galacticware.griddle.domain.model.gesture.GestureType.SWIPE_DOWN_RIGHT
import com.galacticware.griddle.domain.model.gesture.GestureType.SWIPE_LEFT
import com.galacticware.griddle.domain.model.gesture.GestureType.SWIPE_RIGHT
import com.galacticware.griddle.domain.model.gesture.GestureType.SWIPE_UP
import com.galacticware.griddle.domain.model.gesture.GestureType.SWIPE_UP_LEFT
import com.galacticware.griddle.domain.model.gesture.GestureType.SWIPE_UP_RIGHT
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.BACKSPACE
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.CHOOSE_DIFFERENT_INPUT_METHOD
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.COPY
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.CUT
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.DOWN_ARROW
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.GO
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.MICROPHONE_CHAR
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.MOVE_END
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.MOVE_HOME
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.MOVE_PGDN
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.MOVE_PGUP
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.PASTE
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.REPEAT
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.RIGHT_ARROW
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.SELECT_ALL_TEXT
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.GLOBAL_SETTINGS
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.SWAP_HANDEDNESS
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.TAB_RIGHT
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.UP_ARROW
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.create
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.changeModifier
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.changeUserSetting
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.pressKey
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.remappedSymbolLookup
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.switchLayer
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.switchScreens
import com.galacticware.griddle.domain.model.keyboard.definition.designs.constant.IGNORE_SHIFT
import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.modifier.AppModifierKey.Companion.control
import com.galacticware.griddle.domain.model.operation.implementation.noargs.accentchars.CycleAccentCharacters
import com.galacticware.griddle.domain.model.operation.implementation.noargs.backspace.Backspace
import com.galacticware.griddle.domain.model.operation.implementation.noargs.backspace.HotSwapControlBackspace
import com.galacticware.griddle.domain.model.operation.implementation.noargs.changeinputmethod.ChangeInputMethod
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveEnd
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveHome
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveLeft
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveRight
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveWordLeft
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveWordRight
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.SelectAll
import com.galacticware.griddle.domain.model.operation.implementation.noargs.hidekeyboard.HideKeyboard
import com.galacticware.griddle.domain.model.operation.implementation.noargs.noop.NoOp
import com.galacticware.griddle.domain.model.operation.implementation.noargs.repeat.Repeat
import com.galacticware.griddle.domain.model.operation.implementation.noargs.simpleinput.SimpleInput
import com.galacticware.griddle.domain.model.operation.implementation.noargs.speechtotext.SpeechToText
import com.galacticware.griddle.domain.model.operation.implementation.noargs.swaphandedness.SwapHandedness
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifierArgs.Companion.ForwardCycleAlt
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifierArgs.Companion.ForwardCycleControl
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifierArgs.Companion.ForwardCycleShift
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifierArgs.Companion.ReleaseShift
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifierArgs.Companion.ToggleAltRepeat
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifierArgs.Companion.ToggleControlRepeat
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifierArgs.Companion.ToggleShiftRepeat
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changeusersettings.ChangeUserSettingArgs.Companion.ToggleTurboMode
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressEnterKey
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressTab
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.SendNewLineFeed
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.SendTab
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreenArgs
import com.galacticware.griddle.domain.model.util.caseSensitive
import com.galacticware.griddle.domain.model.util.reversedCase
import com.galacticware.griddle.domain.model.util.triple

/**
 * These GestureButtonBuilders are positioned in the traditional MessagEase layout positions.
 */

object GriddleButtonBuilders {
    val englishA by lazy {
        gestureButton(rowStart = 0, colStart = 0, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(CLICK, SimpleInput, threeStrings = caseSensitive("a")),
                create(HOLD, SimpleInput, label = "1"),
                create(SWIPE_UP_LEFT, CycleAccentCharacters, AppSymbol.CYCLE_ACCENTED_CHARS),
                create(SWIPE_RIGHT, SimpleInput, threeStrings = caseSensitive("-", "÷", "÷")),
                create(BOOMERANG_RIGHT, SimpleInput, threeStrings = reversedCase("÷", "-", "-")),
                create(SWIPE_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive("$")),
                create(BOOMERANG_DOWN_LEFT, SimpleInput, threeStrings = reversedCase("¢")),
                create(SWIPE_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive("v")),
                create(BOOMERANG_DOWN_RIGHT, SimpleInput, threeStrings = reversedCase("V")),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("A")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("A")),
            )
        )
    }

    val englishN by lazy {
        gestureButton(rowStart = 0, colStart = 1, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(BOOMERANG_UP, SimpleInput, threeStrings = caseSensitive("ˇ")),
                create(CLICK, SimpleInput, threeStrings = caseSensitive("n")),
                create(HOLD, SimpleInput, threeStrings = triple("2")),
                create(SWIPE_UP_LEFT, SimpleInput, threeStrings = caseSensitive("`", "\\", "\\")),
                create(BOOMERANG_UP_LEFT, SimpleInput, threeStrings = caseSensitive("\\", "`", "`")),
                create(SWIPE_UP, SimpleInput, threeStrings = caseSensitive("^", "’", "’")),
                create(SWIPE_UP_RIGHT, SimpleInput, threeStrings = Triple("^", "’", "’")),
                create(SWIPE_RIGHT, SimpleInput, threeStrings = caseSensitive("!", "¡", "¡")),
                create(BOOMERANG_RIGHT, SimpleInput, threeStrings = caseSensitive("¡", "!", "!")),
                create(SWIPE_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive("/", "—", "—")),
                create(BOOMERANG_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive("—", "/", "/")),
                create(SWIPE_DOWN, SimpleInput, threeStrings = caseSensitive("l")),
                create(BOOMERANG_DOWN, SimpleInput, threeStrings = reversedCase("L")),
                create(SWIPE_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive("/", "—", "—")),
                create(BOOMERANG_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive("—", "/", "/")),
                create(SWIPE_LEFT, SimpleInput, threeStrings = caseSensitive("+", "×", "×")),
                create(BOOMERANG_LEFT, SimpleInput, threeStrings = caseSensitive("×", "+", "+")),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("n")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("n")),
            )
        )
    }


    val englishI by lazy {
        gestureButton(rowStart = 0, colStart = 2, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(CLICK, SimpleInput, threeStrings = caseSensitive("i")),
                create(SWIPE_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive("€", "£", "£")),
                create(BOOMERANG_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive("£", "€", "€")),
                create(SWIPE_DOWN, SimpleInput, threeStrings = caseSensitive("=", "±", "±")),
                create(BOOMERANG_DOWN, SimpleInput, threeStrings = caseSensitive("±", "=", "=")),
                create(SWIPE_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive("x")),
                create(BOOMERANG_DOWN_LEFT, SimpleInput, threeStrings = reversedCase("X")),
                create(SWIPE_LEFT, SimpleInput, threeStrings = caseSensitive("?", "¿", "¿")),
                create(BOOMERANG_LEFT, SimpleInput, threeStrings = caseSensitive("¿", "?", "?")),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("I")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("I")),
                create(HOLD, SimpleInput, threeStrings = triple("3")),
                pressKey(SWIPE_UP, KEYCODE_DPAD_UP, appSymbol = UP_ARROW),
                pressKey(BOOMERANG_UP, KEYCODE_PAGE_UP, appSymbol = MOVE_PGUP),
            )
        )
    }

    val englishH by lazy {
        gestureButton(rowStart = 1, colStart = 0, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                changeModifier(SWIPE_UP, ForwardCycleShift),
                changeModifier(SWIPE_DOWN, ReleaseShift),
                create(SWIPE_UP_LEFT, SimpleInput, threeStrings = Triple("{", "}", "}")),
                create(BOOMERANG_UP_LEFT, SimpleInput, threeStrings = Triple("}", "{", "{")),
                changeModifier(BOOMERANG_UP, ToggleShiftRepeat),
                create(SWIPE_UP_RIGHT, SimpleInput, threeStrings = Triple("%", "‰", "‰")),
                create(BOOMERANG_UP_RIGHT, SimpleInput, threeStrings = Triple("‰", "%", "%")),
                create(SWIPE_RIGHT, SimpleInput, threeStrings = caseSensitive("k")),
                create(BOOMERANG_RIGHT, SimpleInput, threeStrings = reversedCase("K")),
                create(SWIPE_DOWN_RIGHT, SimpleInput, threeStrings = Triple("_", "¬", "¬")),
                create(BOOMERANG_DOWN_RIGHT, SimpleInput, threeStrings = Triple("¬", "_", "_")),
                create(SWIPE_DOWN_LEFT, SimpleInput, threeStrings = Triple("[", "]", "]")),
                create(BOOMERANG_DOWN_LEFT, SimpleInput, threeStrings = Triple("]", "[", "[")),
                create(SWIPE_LEFT, SimpleInput, threeStrings = Triple("(", ")", ")")),
                create(BOOMERANG_LEFT, SimpleInput, threeStrings = Triple(")", "(", "(")),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("H")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("H")),
                create(HOLD, SimpleInput, threeStrings = caseSensitive("4")),
                create(CLICK, SimpleInput, threeStrings = caseSensitive("h")),
            )
        )
    }

    val englishO by lazy {
        gestureButton(rowStart = 1, colStart = 1, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(CLICK, SimpleInput, threeStrings = caseSensitive("o")),
                create(SWIPE_UP_LEFT, SimpleInput, threeStrings = caseSensitive("q")),
                create(BOOMERANG_UP_LEFT, SimpleInput, threeStrings = reversedCase("q")),
                create(SWIPE_UP, SimpleInput, threeStrings = caseSensitive("u")),
                create(BOOMERANG_UP, SimpleInput, threeStrings = reversedCase("u")),
                create(SWIPE_UP_RIGHT, SimpleInput, threeStrings = caseSensitive("p")),
                create(BOOMERANG_UP_RIGHT, SimpleInput, threeStrings = reversedCase("P")),
                create(SWIPE_RIGHT, SimpleInput, threeStrings = caseSensitive("b")),
                create(BOOMERANG_RIGHT, SimpleInput, threeStrings = reversedCase("B")),
                create(SWIPE_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive("j")),
                create(BOOMERANG_DOWN_RIGHT, SimpleInput, threeStrings = reversedCase("J")),
                create(SWIPE_DOWN, SimpleInput, threeStrings = caseSensitive("d")),
                create(BOOMERANG_DOWN, SimpleInput, threeStrings = reversedCase("D")),
                create(SWIPE_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive("g")),
                create(BOOMERANG_DOWN_LEFT, SimpleInput, threeStrings = reversedCase("G")),
                create(SWIPE_LEFT, SimpleInput, threeStrings = caseSensitive("c")),
                create(BOOMERANG_LEFT, SimpleInput, threeStrings = reversedCase("C")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("O")),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("O")),
                create(HOLD, SimpleInput, threeStrings = reversedCase("5")),
            )
        )
    }

    val englishR by lazy {
        gestureButton(rowStart = 1, colStart = 2, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                changeModifier(SWIPE_UP, ForwardCycleShift),
                changeModifier(SWIPE_DOWN, ReleaseShift),
                create(SWIPE_UP_LEFT, SimpleInput, threeStrings = caseSensitive("|", "\\", "\\")),
                create(BOOMERANG_UP_LEFT, SimpleInput, threeStrings = caseSensitive("\\", "|", "|")),
                changeModifier(BOOMERANG_UP, ToggleShiftRepeat),
                create(SWIPE_UP_RIGHT, SimpleInput, threeStrings = caseSensitive("}", "{", "{")),
                create(BOOMERANG_UP_RIGHT, SimpleInput, threeStrings = caseSensitive("{", "}", "}")),
                create(SWIPE_RIGHT, SimpleInput, threeStrings = caseSensitive(")", "(", "(")),
                create(BOOMERANG_RIGHT, SimpleInput, threeStrings = caseSensitive("(", ")", ")")),
                create(SWIPE_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive("]", "[", "[")),
                create(BOOMERANG_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive("[", "]", "]")),
                create(BOOMERANG_DOWN, SimpleInput, threeStrings = caseSensitive("—")),
                create(SWIPE_DOWN_LEFT, SimpleInput, label = "@"),
                create(BOOMERANG_DOWN_LEFT, SimpleInput, threeStrings = Triple("ª", "@", "@")),
                create(SWIPE_LEFT, SimpleInput, threeStrings = caseSensitive("m")),
                create(BOOMERANG_LEFT, SimpleInput, threeStrings = reversedCase("M")),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("R")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("R")),
                create(CLICK, SimpleInput, threeStrings = caseSensitive("r")),
                create(HOLD, SimpleInput, threeStrings = reversedCase("6")),
            )
        )
    }

    val englishT by lazy {
        gestureButton(rowStart = 2, colStart = 0, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(CLICK, SimpleInput, threeStrings = caseSensitive("t")),
                create(HOLD, SimpleInput, threeStrings = reversedCase("7")),
                create(SWIPE_UP_LEFT, SimpleInput, threeStrings = caseSensitive("~")),
                create(SWIPE_UP, SimpleInput, threeStrings = Triple("¨", "¨", "¨")),
                create(BOOMERANG_UP, SimpleInput, threeStrings = Triple("˝", "¨", "¨")),
                create(SWIPE_UP_RIGHT, SimpleInput, threeStrings = caseSensitive("y")),
                create(BOOMERANG_UP_RIGHT, SimpleInput, threeStrings = reversedCase("Y")),
                create(SWIPE_RIGHT, SimpleInput, threeStrings = Triple("*", "†", "†")),
                create(BOOMERANG_RIGHT, SimpleInput, threeStrings = Triple("†", "*", "*")),
                create(SWIPE_LEFT, SimpleInput, threeStrings = Triple("<", ">", ">")),
                create(BOOMERANG_LEFT, SimpleInput, threeStrings = Triple(">", "<", "<")),
                create(SWIPE_DOWN_RIGHT, SendTab, threeStrings = triple(TAB_RIGHT)),
                create(BOOMERANG_DOWN_RIGHT, PressTab, threeStrings = triple(TAB_RIGHT)),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("T")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("T")),
            )
        )
    }

    val englishE by lazy {
        gestureButton(rowStart = 2, colStart = 1, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(SWIPE_UP_LEFT, SimpleInput, threeStrings = caseSensitive("\"")),
                create(BOOMERANG_UP_LEFT, SimpleInput, threeStrings = caseSensitive(" ")),
                create(SWIPE_UP, SimpleInput, threeStrings = caseSensitive("w")),
                create(BOOMERANG_UP, SimpleInput, threeStrings = reversedCase("W")),
                create(SWIPE_UP_RIGHT, SimpleInput, threeStrings = caseSensitive("\'")),
                create(BOOMERANG_UP_RIGHT, SimpleInput, threeStrings = caseSensitive("\'")),
                create(SWIPE_RIGHT, SimpleInput, threeStrings = caseSensitive("z")),
                create(BOOMERANG_RIGHT, SimpleInput, threeStrings = reversedCase("Z")),
                create(SWIPE_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive(":")),
                create(BOOMERANG_DOWN_RIGHT, SimpleInput, threeStrings = caseSensitive(",")),
                create(SWIPE_DOWN, SimpleInput, threeStrings = caseSensitive(".", "…", "…")),
                create(BOOMERANG_DOWN, SimpleInput, threeStrings = caseSensitive("…", ".", ".")),
                create(SWIPE_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive(",")),
                create(BOOMERANG_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive(",")),
                create(SWIPE_LEFT, SimpleInput, threeStrings = caseSensitive(",")),
                create(BOOMERANG_LEFT, SimpleInput, threeStrings = caseSensitive(",")),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("E")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("E")),
                create(CLICK, SimpleInput, threeStrings = caseSensitive("e")),
                create(HOLD, SimpleInput, label = "8"),
            )
        )
    }

    val englishS by lazy {
        gestureButton(rowStart = 2, colStart = 2, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(CLICK, SimpleInput, threeStrings = caseSensitive("s")),
                create(SWIPE_UP_LEFT, SimpleInput, threeStrings = caseSensitive("f")),
                create(BOOMERANG_UP_LEFT, SimpleInput, threeStrings = reversedCase("F")),
                create(SWIPE_UP, SimpleInput, threeStrings = caseSensitive("&")),
                create(SWIPE_RIGHT, SimpleInput, threeStrings = caseSensitive(">")),
                create(SWIPE_UP_RIGHT, SimpleInput, threeStrings = caseSensitive("°")),
                create(SWIPE_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive(";")),
                create(SWIPE_DOWN, SendNewLineFeed, appSymbol = AppSymbol.NEW_LINE),
                create(BOOMERANG_DOWN_LEFT, SimpleInput, threeStrings = caseSensitive(";")),
                create(SWIPE_LEFT, SimpleInput, threeStrings = caseSensitive("#")),
                create(BOOMERANG_LEFT, SimpleInput, threeStrings = caseSensitive(" ")),
                create(CIRCLE_CLOCKWISE, SimpleInput, threeStrings = reversedCase("S")),
                create(CIRCLE_ANTI_CLOCKWISE, SimpleInput, threeStrings = reversedCase("S")),
                create(HOLD, SimpleInput, threeStrings = caseSensitive("9")),
                create(CLICK, SimpleInput, threeStrings = caseSensitive("s")),
                create(HOLD, SimpleInput, threeStrings = reversedCase("9")),
                pressKey(SWIPE_DOWN, KEYCODE_DPAD_DOWN, appSymbol = DOWN_ARROW),
                pressKey(BOOMERANG_DOWN, KEYCODE_PAGE_DOWN, appSymbol = MOVE_PGDN),
            )
        )
    }

    val settingsButton by lazy {
        gestureButton(
            rowStart = 0, colStart = 3, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(CLICK, NoOp, appSymbol = GLOBAL_SETTINGS, isIndicator = true),
                switchScreens(HOLD, SwitchScreenArgs.OpenBaseSettings),
                pressKey(SWIPE_LEFT, KEYCODE_Z, setOf(control), IGNORE_SHIFT, AppSymbol.UNDO),
                pressKey(SWIPE_RIGHT, KEYCODE_Y, setOf(control), IGNORE_SHIFT, AppSymbol.REDO),
                changeUserSetting(SWIPE_UP, ToggleTurboMode),
                switchLayer(CIRCLE_ANTI_CLOCKWISE, LayerKind.UNIFIED_ALPHA_NUMERIC),
                switchLayer(CIRCLE_CLOCKWISE, LayerKind.UNIFIED_ALPHA_NUMERIC),
                create(SWIPE_DOWN, HideKeyboard, )
            ),
        )
    }

    val backspace by lazy {
        gestureButton(
            rowStart = 2, colStart = 3, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(HOLD, Backspace),
                remappedSymbolLookup(CLICK, BACKSPACE).apply { swapAssignment = create(CLICK, Repeat, appSymbol = REPEAT).assignment },
                pressKey(SWIPE_LEFT, KEYCODE_DEL),
                pressKey(SWIPE_UP_LEFT, KEYCODE_DEL),
                pressKey(SWIPE_DOWN_LEFT, KEYCODE_DEL),
                create(BOOMERANG_LEFT, HotSwapControlBackspace),
                pressKey(SWIPE_RIGHT, KEYCODE_FORWARD_DEL),
                pressKey(SWIPE_UP_RIGHT, KEYCODE_FORWARD_DEL),
                pressKey(SWIPE_DOWN_RIGHT, KEYCODE_FORWARD_DEL),
                pressKey(BOOMERANG_RIGHT, KEYCODE_FORWARD_DEL, setOf(control))
            ),
        )
    }
    val AlphabeticLayerToggle by lazy {
        gestureButton(
            rowStart = 1, colStart = 3, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                remappedSymbolLookup(SWIPE_UP, COPY),
                remappedSymbolLookup(SWIPE_LEFT, CUT),
                switchScreens(SWIPE_UP_LEFT, SwitchScreenArgs.OpenTextReplacementEditor),
                remappedSymbolLookup(SWIPE_DOWN, PASTE),
                create(SWIPE_DOWN_LEFT, ChangeInputMethod, appSymbol = CHOOSE_DIFFERENT_INPUT_METHOD),
                switchLayer(CLICK, LayerKind.ALPHA),
                switchLayer(HOLD, LayerKind.NUMERIC),
                switchScreens(SWIPE_UP_RIGHT, SwitchScreenArgs.OpenEmoji),
                create(SWIPE_RIGHT, SwapHandedness, appSymbol = SWAP_HANDEDNESS),
                pressKey(CIRCLE_ANTI_CLOCKWISE, KEYCODE_A, setOf(control)),
                pressKey(CIRCLE_CLOCKWISE, KEYCODE_A, setOf(control)),
                create(SWIPE_DOWN_RIGHT, SpeechToText, appSymbol = MICROPHONE_CHAR),
                switchScreens(BOOMERANG_DOWN, SwitchScreenArgs.OpenClipboard),
            ),
        )
    }
    val NumericLayerToggle by lazy {
        gestureButton(
            rowStart = 1, colStart = 3, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                remappedSymbolLookup(SWIPE_UP, COPY),
                remappedSymbolLookup(SWIPE_LEFT, CUT),
                remappedSymbolLookup(SWIPE_DOWN, PASTE),
                create(SWIPE_DOWN_LEFT, ChangeInputMethod, appSymbol = CHOOSE_DIFFERENT_INPUT_METHOD),
                switchScreens(SWIPE_UP_LEFT, SwitchScreenArgs.OpenTextReplacementEditor),
                switchScreens(SWIPE_UP_RIGHT, SwitchScreenArgs.OpenEmoji),
                create(SWIPE_RIGHT, SwapHandedness, appSymbol = SWAP_HANDEDNESS),
                switchLayer(CLICK, LayerKind.NUMERO_SYMBOLIC),
                switchLayer(HOLD, LayerKind.NUMERIC),
                create(CIRCLE_ANTI_CLOCKWISE, SelectAll, appSymbol = SELECT_ALL_TEXT),
                create(CIRCLE_CLOCKWISE, SelectAll, appSymbol = SELECT_ALL_TEXT),
                create(SWIPE_DOWN_RIGHT, SpeechToText, appSymbol = MICROPHONE_CHAR),
                switchScreens(BOOMERANG_DOWN, SwitchScreenArgs.OpenClipboard),
            ),
        )
    }
    val enter by lazy {
        gestureButton(
            rowStart = 3, colStart = 3, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(CLICK, PressEnterKey, appSymbol = GO),
                changeModifier(SWIPE_UP_LEFT, ForwardCycleAlt),
                changeModifier(BOOMERANG_UP_LEFT, ToggleAltRepeat),
                changeModifier(SWIPE_UP_RIGHT, ForwardCycleControl),
                changeModifier(BOOMERANG_UP_RIGHT, ToggleControlRepeat),
                create(SWIPE_DOWN, HideKeyboard, AppSymbol.HIDE_KEYBOARD)
            ),
        )
    }

    val space2u by lazy {
        gestureButton(
            rowStart = 3, colStart = 1, rowSpan = 1, colSpan = 2,
            gestureSet = mutableSetOf(
                pressKey(CLICK, KEYCODE_SPACE, respectShift = IGNORE_SHIFT),
                switchLayer(SWIPE_DOWN_LEFT, LayerKind.FUNCTION),
                create(HOLD, SimpleInput, threeStrings = reversedCase("0")),
                create(SWIPE_LEFT, MoveLeft, appSymbol = AppSymbol.LEFT_ARROW),
                create(BOOMERANG_LEFT, MoveWordLeft),
                create(SWIPE_RIGHT, MoveRight, appSymbol = RIGHT_ARROW),
                create(BOOMERANG_RIGHT, MoveWordRight),
                create(CIRCLE_CLOCKWISE, MoveEnd, threeStrings = triple(MOVE_END)),
                create(CIRCLE_ANTI_CLOCKWISE, MoveHome, threeStrings = triple(MOVE_HOME)),            ),
        )
    }

    val space3u by lazy {
        space2u.reposition(colStart = 0, colSpan = 3)
    }

    val repeat by lazy {
        gestureButton(
            rowStart = 2, colStart = 3, rowSpan = 1, colSpan = 1,
            gestureSet = mutableSetOf(
                create(CLICK, Repeat, appSymbol = REPEAT),
            )
        )
    }

    val numericSpaceLeft by lazy {
        space2u
            .withoutGesture { g: Gesture -> g.currentText == AppSymbol.GLOBAL_SETTINGS.value }
            .reposition(0, 3, 1, 1)
    }

    val numericSpaceRight by lazy {
        numericSpaceLeft
            .reposition(colStart = 2)
    }
}