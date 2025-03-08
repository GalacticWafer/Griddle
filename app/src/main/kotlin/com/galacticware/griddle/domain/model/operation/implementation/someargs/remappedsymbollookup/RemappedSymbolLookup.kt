package com.galacticware.griddle.domain.model.operation.implementation.someargs.remappedsymbollookup

import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.ExtractedTextRequest
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.usercontolled.userdefinedgesturemapping.ReassignmentData
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel
import kotlinx.serialization.json.Json

/**
 * For KeyBindings that have special visual symbols that differ from the key they represent, such as
 * special keys (arrow keys, space keys, layer switches, etc.)
 */
object RemappedSymbolLookup :ParameterizedOperation<RemappedSymbolLookupArgs>({ k -> RemappedSymbolLookup.executeOperation(k) }) {
    override val tag: OperationTag
        get() = OperationTag.REMAPPED_SYMBOL_LOOKUP
    override val menuItemDescription: String
        get() = "Assign a Special key to this gesture"

    override fun provideArgs(jsonString: String) =
        Json.decodeFromString<RemappedSymbolLookupArgs>(jsonString)

    @Composable
    override fun ShowArgsFinalizationScreen(context: Context, gesture: Gesture) {

    }

    override val userHelpDescription: String
        get() = "Choose a special key to assign to this gesture"
    override val requiresUserInput: Boolean
        get() = true

    @Composable
    override fun ShowReassignmentScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        val initiationRequest =
            byokViewModel.reassignmentDataStateFlow.collectAsState().value
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            val symbols = AppSymbol.specialKeySymbols.entries.toList()
            items(symbols.size) { index ->
                val (symbol, keycode) = symbols[index]
                Row {
                    Button(
                        modifier = Modifier,
                        onClick = {
                            byokViewModel.setReassignmentData(
                                ReassignmentData(
                                    initiationRequest!!.draftGesture,
                                    OperationTag.REMAPPED_SYMBOL_LOOKUP.objectInstance,
                                    RemappedSymbolLookupArgs(symbol)
                                )
                            )
                        },
                    ) {
                        Text(symbol.uiLabel)
                        Text("  -->  ")
                        Text("'keycode $keycode'")
                    }
                }
            }
        }
    }

    override fun produceNewGesture(gesturePrototype: Gesture): Gesture {
        return super.produceNewGestureWithAppSymbol(
            gesturePrototype,
            this,
            gesturePrototype.assignment.appSymbol!!
        )
    }

    override fun executeOperation(keyboardContext: KeyboardContext) {
        val inputConnection = keyboardContext.inputConnection
        val edit = inputConnection.getExtractedText(
            ExtractedTextRequest(), 0
        )
        if (edit != null) {
            keyboardContext.gesture.currentAssignment.appSymbol?.let { appSymbol ->
                val (string) = keyboardContext.remappedSymbolArgs
                inputConnection.sendRemappedSymbol(
                    KeyEvent(
                        0,
                        System.currentTimeMillis(),
                        KeyEvent.ACTION_DOWN,
                        AppSymbol.specialKeySymbols[string]!!,
                        0,
                        appSymbol.keyEventMasks.fold(0) { acc, num -> acc or num }
                    ),
                )
            }
        }
    }
}