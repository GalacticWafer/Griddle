package com.galacticware.griddle.domain.model.gesture

import android.content.Context
import android.view.View
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.shared.Point
import com.galacticware.griddle.domain.model.input.GriddleInputConnection
import com.galacticware.griddle.domain.model.input.IMEService
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.SavedKeyboardFileType
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifier
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changeusersettings.ChangeUserSetting
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKey
import com.galacticware.griddle.domain.model.operation.implementation.someargs.remappedsymbollookup.RemappedSymbolLookup
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchlayer.SwitchLayer
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreens

/**
 * [KeyboardContext] is the object holding all information needed for an [Operation] function to
 * execute whatever it does upon user input.
 * Todo forever: put more data into this object as needed to execute new [Operation] logic.
 */
data class KeyboardContext(
    var keyboard: Keyboard,
    val context: Context,
    val gesture: Gesture,
    val touchPoints: List<Point>,
    val view: View? = null,
    val previousOperation: (() -> Unit)?,
    val gestureButtonPosition: GridPosition,
) {
    fun switchKeyboards(keyboard: Keyboard) {
        this.keyboard = keyboard
        this.keyboard.saveKeyboardFile(context, SavedKeyboardFileType.JSON)
    }

    val causesTextReplacementRedaction: Boolean get() = pressKeyArgs.causesTextReplacementRedaction
    val isPeripheral: Boolean get() = gesture.currentAssignment.isPeripheral
    val inputConnection: GriddleInputConnection = GriddleInputConnection(
        (context as IMEService).currentInputConnection,
        context
    )
    val currentText get() = gesture.currentText
    val oneShotMetaState get() = gesture.oneShotMetaState
    private val argsAsJson get() =  gesture.currentAssignment.argsJson
    val pressKeyArgs get() = PressKey.provideArgs(argsAsJson!!)
    val switchLayerArgs get() = SwitchLayer.provideArgs(argsAsJson!!)
    val modifierKeyHandlerArgs get() = ChangeModifier.provideArgs(argsAsJson!!)
    val changeUserSettingArgs get() = ChangeUserSetting.provideArgs(argsAsJson!!)
    val switchScreenArgs get() = SwitchScreens.provideArgs(argsAsJson!!)
    val remappedSymbolArgs get() = RemappedSymbolLookup.provideArgs(argsAsJson!!)
}

abstract class VariableOperation(f: ((KeyboardContext) ->Unit)?): Operation(f?:{})