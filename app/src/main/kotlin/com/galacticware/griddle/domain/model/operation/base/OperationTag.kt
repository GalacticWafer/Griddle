package com.galacticware.griddle.domain.model.operation.base

import com.galacticware.griddle.domain.model.error.UnsupportedOperationRemappingException
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.operation.implementation.noargs.accentchars.CycleAccentCharacters
import com.galacticware.griddle.domain.model.operation.implementation.noargs.backspace.Backspace
import com.galacticware.griddle.domain.model.operation.implementation.noargs.changeinputmethod.ChangeInputMethod
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveEnd
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveHome
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveLeft
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MovePageDown
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MovePageUp
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveRight
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveWordLeft
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.MoveWordRight
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.SelectAll
import com.galacticware.griddle.domain.model.operation.implementation.noargs.noop.NoOp
import com.galacticware.griddle.domain.model.operation.implementation.noargs.repeat.Repeat
import com.galacticware.griddle.domain.model.operation.implementation.noargs.resizeboard.ResizeBoard
import com.galacticware.griddle.domain.model.operation.implementation.noargs.simpleinput.SimpleInput
import com.galacticware.griddle.domain.model.operation.implementation.noargs.speechtotext.SpeechToText
import com.galacticware.griddle.domain.model.operation.implementation.noargs.swaphandedness.SwapHandedness
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifier
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changeusersettings.ChangeUserSetting
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressEnterKey
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKey
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressTab
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.SendNewLineFeed
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.SendTab
import com.galacticware.griddle.domain.model.operation.implementation.someargs.remappedsymbollookup.RemappedSymbolLookup
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchlayer.SwitchLayer
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreens

/**
 * All operations which have side effects to the keyboard state must be included in this enum,
 * or represented by the SYSTEM_OPERATION enum.
 */
enum class OperationTag(val objectInstance: Operation) {
    SIMPLE_INPUT(SimpleInput),
    RESIZE_BOARD(ResizeBoard),
    CYCLE_ACCENT_CHARACTERS(CycleAccentCharacters),
    START_SPEECH_RECOGNITION(SpeechToText),

    SWITCH_SCREENS(SwitchScreens),
    REMAPPED_SYMBOL_LOOKUP(RemappedSymbolLookup),
    CHANGE_USER_SETTING(ChangeUserSetting),
    PRESS_KEY(PressKey),
    SWITCH_LAYER(SwitchLayer),

    BACKSPACE(Backspace),
    /** FIXME: not sure what happened, but repeat doesn't work anymore. */
    REPEAT_PREVIOUS_OPERATION(Repeat),
    SWAP_HANDEDNESS(SwapHandedness),
    NO_OP(NoOp),
    CHANGE_MODIFIER(ChangeModifier),
    /** FIXME: not sure what happened, but multi-operation doesn't work anymore. */
//    MULTI_OPERATION(MultiOperation),

    SELECT_ALL(SelectAll),
    MOVE_HOME(MoveHome),
    MOVE_END(MoveEnd),
    MOVE_PAGE_UP(MovePageUp),
    MOVE_PAGE_DOWN(MovePageDown),
    MOVE_LEFT(MoveLeft),
    MOVE_RIGHT(MoveRight),
    MOVE_ONE_WORD_LEFT(MoveWordLeft),
    MOVE_ONE_WORD_RIGHT(MoveWordRight),
    NEW_LINE(SendNewLineFeed),
    PressEnter(PressEnterKey),
    SEND_TAB(SendTab),
    PRESS_TAB(PressTab),
    ENTER(PressEnterKey),
    CHANGE_INPUT_METHOD(ChangeInputMethod)
    ;

    companion object {
        val userConfigurableOperations: Set<Operation> get() = entries
            .mapNotNull {
                try {
                    it.objectInstance.let { op ->
                        op.tag
                        op.userHelpDescription
                        op.menuItemDescription
                        op.name
                        op.requiresUserInput
                    }
                    it.objectInstance
                } catch (e: UnsupportedOperationRemappingException) {
                    null
                }
            }.toSet()
    }
}