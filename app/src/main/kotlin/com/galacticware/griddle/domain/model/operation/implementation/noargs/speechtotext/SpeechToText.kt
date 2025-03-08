package com.galacticware.griddle.domain.model.operation.implementation.noargs.speechtotext

import android.content.Context
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.util.SpeechRecognitionDelegate

object SpeechToText: Operation({ k -> SpeechToText.operate(k) }) {
    override fun executeOperation(keyboardContext: KeyboardContext) {
        operate(keyboardContext)
    }
    override val name get() = "Resize & Move keyboard"
    override val tag get() = OperationTag.START_SPEECH_RECOGNITION
    override val menuItemDescription get() = "Talk-to-text"
    override val userHelpDescription get() = "Activate speech-to-text instead."
    override val requiresUserInput: Boolean get() = false
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture =
        produceNewGestureWithAppSymbol(gesturePrototype, this, AppSymbol.SPEECH_TO_TEXT)
    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
            = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to \"Start speech recognition\"?")

    private fun operate(keyboardContext: KeyboardContext) {
        SpeechRecognitionDelegate(keyboardContext.context).listen()
    }
}