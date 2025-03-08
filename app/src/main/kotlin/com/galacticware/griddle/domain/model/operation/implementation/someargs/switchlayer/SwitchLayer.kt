package com.galacticware.griddle.domain.model.operation.implementation.someargs.switchlayer

import android.content.Context
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import kotlinx.serialization.json.Json

object SwitchLayer :  ParameterizedOperation<SwitchLayerArgs>({ k -> SwitchLayer.executeOperation(k) }) {
    override fun provideArgs(jsonString: String): SwitchLayerArgs =
        Json.decodeFromString<SwitchLayerArgs>(jsonString)

    @Composable
    override fun ShowArgsFinalizationScreen(context: Context, gesture: Gesture) {
        TODO("Not yet implemented")
    }

    override val menuItemDescription: String
        get() = "Switch to another layer"
    override val requiresUserInput: Boolean
        get() = true
    override val userHelpDescription: String
        get() = "Choose another layer to switch to"

    @Composable
    override fun ShowReassignmentScreen(context: Context, gesture: Gesture) {
        super.ShowReassignmentScreen(context, gesture)
    }
    override val tag: OperationTag
        get() = OperationTag.SWITCH_LAYER
    override val name: String
        get() = "SwitchLayer"
    override fun executeOperation(keyboardContext: KeyboardContext) {
        val (layerName, layerKind) = keyboardContext.switchLayerArgs
        layerName?.let {
            keyboardContext.keyboard.switchToLayerByName(layerName)
        }?: run {
            (layerKind?: run {
                val symbol = keyboardContext.gesture.currentAssignment.appSymbol!!
                LayerKind.fromAlias(symbol)
            }).let {
                keyboardContext.keyboard.switchToLayerKind(it)
            }
        }
    }
}
