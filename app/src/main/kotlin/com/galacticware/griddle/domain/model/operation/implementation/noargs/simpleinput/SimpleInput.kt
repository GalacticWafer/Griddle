package com.galacticware.griddle.domain.model.operation.implementation.noargs.simpleinput

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.*
import android.view.inputmethod.InputConnection
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.PlatformTextInputMethodRequest
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.input.AppInputFocus
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.create
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.textreplacement.TextReplacement
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel

/**
 * Sends the exact characters saved to the keyboardContext.gesture.assignment.currentTheme()
 */
object SimpleInput : Operation({ SimpleInput.executeOperation(it) }) {
    override val userHelpDescription by lazy { "Type or paste one or more characters to reassign this gesture." }
    override val menuItemDescription by lazy { "Send a different character (or characters)" }
    override val name by lazy { "Simple input" }
    override val tag by lazy { OperationTag.SIMPLE_INPUT }
    override val requiresUserInput by lazy { true }
    var text = ""
    override fun produceNewGesture (gesturePrototype: Gesture): Gesture {
        return create(
            GestureType.fromInstance(gesturePrototype),
            this,
            gesturePrototype.currentAssignment.appSymbol,
            label = text,
            foregroundColor = gesturePrototype.currentAssignment.currentTheme.primaryTextColor,
            backgroundColor = gesturePrototype.currentAssignment.currentTheme.primaryBackgroundColor,
            borderColor = gesturePrototype.currentAssignment.currentTheme.primaryBorderColor,
            modifierTheme = gesturePrototype.currentAssignment.currentTheme,
            modifiers = gesturePrototype.currentAssignment.modifiers,
            modifierThemeSet = gesturePrototype.currentAssignment.modifierThemeSet,
        ).let {
            it.assignment = it.currentAssignment.withText(BuildYourOwnKeyboardViewModel.editableInputCallbackMap[AppInputFocus.SIMPLE_INPUT_ASSIGNMENT]!!.value())
            it
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun ShowReassignmentScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        byokViewModel.setIsWaitingForGestureInfo(true)
        // Simple input should have a small text field to take in the replacement string
        val initiationRequest = byokViewModel.reassignmentDataStateFlow.collectAsState().value?: run {
            byokViewModel.setReassignmentData(null)
            return
        }

        Row {
            var text by remember { mutableStateOf("") }

            // Use InterceptPlatformTextInput to intercept input and modify behavior
            InterceptPlatformTextInput(
                interceptor = { request, nextHandler ->
                    // Custom logic to modify the input session or the request
                    val modifiedRequest =
                        object : PlatformTextInputMethodRequest {
                            override fun createInputConnection(outAttributes: EditorInfo): InputConnection {
                                // Create the input connection with any custom attributes
                                val inputConnection = request.createInputConnection(outAttributes)

                                // Customize the EditorInfo or InputConnection if necessary
                                updateEditorInfo(outAttributes)

                                return inputConnection
                            }

                            private fun updateEditorInfo(outAttributes: EditorInfo) {
                                // Here you can modify the input attributes or customize the keyboard behavior
                                // For example, set the input type to search, or alter the input connection
                                outAttributes.imeOptions = IME_ACTION_SEARCH
                            }
                        }

                    // Pass the modified request to the next handler in the chain
                    nextHandler.startInputMethod(modifiedRequest)
                }
            ) {
                // The BasicTextField or any text input composable
                TextField(
                    value = text,
                    modifier = Modifier
                        .border(10.dp, Color.Black),
                    onValueChange = {
                        text = it
                        //byokViewModel.setUserDefinedGestureMappingFinalizationRequest(UserDefinedGestureMapping.FinalizationRequest(initiationRequest, it))
                    },
                )
            }


            Button(
                onClick = {
                val currentAssignmentText = initiationRequest.draftGesture.currentAssignment.currentText
                    val gestureType = GestureType.fromInstance(initiationRequest.draftGesture).optionsLabel
                    val s = if(currentAssignmentText.isEmpty()) "Are you sure you want to assign \"$text\" to the $gestureType gesture?"
                        else "Are you sure you want to replace \"$currentAssignmentText\" with \"$text\" on the $gestureType gesture?"
                    byokViewModel.setAskForConfirmation(s)
            }) {
                Text("Done")
            }
        }
//        BuildYourOwnKeyboardScreen.isWaitingForTextInput = true
        Keyboard.currentLayer = PreferencesHelper.getPrimaryAlphaLayer(context)
        byokViewModel.setShouldShowKeyboard(true)
    }
    var currentText: String = ""

    /**
     * SimpleInput has an anonymous Operation that checks for text replacements, then directly
     * sends `keyboardContext.currentText` to be appended to the currently edited text field.
     */
    override fun executeOperation(keyboardContext: KeyboardContext) {
            TextReplacement.checkForReplacementAndThen(keyboardContext, operation = object : Operation({}){
                override fun executeOperation(keyboardContext: KeyboardContext) {
                    keyboardContext.inputConnection.commitText(keyboardContext.currentText)
                }
            })
        }
}