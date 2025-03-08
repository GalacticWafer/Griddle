package com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.currentState
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.savedTextReplacement
import com.galacticware.griddle.domain.model.textreplacement.TextReplacement
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementUndoState.NONE
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.usercontolled.userdefinedgesturemapping.ReassignmentData
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel
import kotlinx.serialization.json.Json

object PressKey :  ParameterizedOperation<PressKeyArgs>({ kc -> TextReplacement.checkForReplacementAndThen(kc, PressKey) }) {
    override val menuItemDescription: String
        get() = userHelpDescription
    override val appSymbol: AppSymbol?
        get() = null
    override var isBackspace: Boolean
        get() = false
        set(value) {}
    override val requiresUserInput: Boolean
        get() = false

    override fun provideArgs(jsonString: String): PressKeyArgs =
        Json.decodeFromString<PressKeyArgs>(jsonString)

    @Composable
    override fun ShowArgsFinalizationScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        Column {
            Text("Choose which screen this gesture should switch to.")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                PressKeyArgs.instances.forEach { changeUserSettingArgs ->
                    item {
                        Button(
                            onClick = {
                                val request = ReassignmentData(
                                    draftGesture = gesture.apply {
                                        assignment.withArgs(changeUserSettingArgs)
                                            .withOperation(PressKey) // do we even need this line?
                                    },
                                    operation = PressKey,
                                    args = changeUserSettingArgs,
                                )
                                byokViewModel.setReassignmentData(request)
                                byokViewModel.setAskForConfirmation("Are you sure you want to replace this gesture with '$PressKey-$changeUserSettingArgs'?")
                            }
                        ) {
                            Text(
                                text = changeUserSettingArgs.description(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            )
                        }
                    }
                }
            }
        }

    }

    override val shouldKeepDuringTurboMode: Boolean get() = true
    override val userHelpDescription: String get() = "Press a key"
    override val tag get() = OperationTag.PRESS_KEY

        private fun operate(
            keyboardContext: KeyboardContext,
        ) {
            val (keycode,respectShift,overrideMetaState,modifierKeys) = keyboardContext.pressKeyArgs
            val metaState = modifierKeys.fold(0) { acc, modifierKey ->
                acc or modifierKey.intValue
            }.let {
                if (overrideMetaState) {
                    it
                } else {
                    it or Keyboard.currentMetaState.value
                }
            }

            keyboardContext.inputConnection.resolveInputRequest(
                keycode,
                null,
                metaState,
                respectShift
            )
        }

    /**
     * If there is a saved text replacement, attempt to redact it. If not, or if
     */
    override fun executeOperation(keyboardContext: KeyboardContext) {
        savedTextReplacement?.let {
            TextReplacement.tryTextReplacementRedaction(keyboardContext)
            currentState = NONE
        } ?: run {
            operate(keyboardContext)
        }
    }
}