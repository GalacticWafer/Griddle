package com.galacticware.griddle.domain.model.operation.implementation.noargs.accentchars

import android.content.Context
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.usercontolled.UserLanguageSelector

object CycleAccentCharacters: Operation({ k -> CycleAccentCharacters.executeOperation(k) }) {
    override val name get() = "Cycle accent characters"
    override val tag get() = OperationTag.CYCLE_ACCENT_CHARACTERS
    override val menuItemDescription get() = "Cycle through accent characters"
    override val userHelpDescription get() = "Change the previously-typed character by cycling through accent characters for the current language."
    override val requiresUserInput get() = false
    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
            = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to \"Cycle through accent characters\"?")
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture =
        produceNewGestureWithAppSymbol(gesturePrototype, this, AppSymbol.CYCLE_ACCENTED_CHARS)

    override fun executeOperation(keyboardContext: KeyboardContext) {
        val inputConnection = keyboardContext.inputConnection
        val et: ExtractedText = inputConnection.getExtractedText(ExtractedTextRequest(), 0)
        val selectionStart = et.selectionStart
        val selectionEnd = et.selectionEnd
        if(selectionEnd == 0 || selectionStart != selectionEnd) {
            return
        }
        // Determine the character before the cursor
        val characterBeforeCursor = et.text[selectionEnd - 1].toString()
        // Check if the character before the cursor is a key from the accent map
        //  for the current language
        for(list in UserLanguageSelector.primaryLanguageTag.accentedCharacters) {
            if(characterBeforeCursor in list) {
                // Get the index of the current character in the list of accent characters
                val currentIndex = list.indexOf(characterBeforeCursor)
                // Get the next accent character
                val nextIndex = (currentIndex + 1) % list.size
                val nextCharacter = list[nextIndex]
                // Replace the current character with the next accent character
                // Delete the character immediately to the left of the caret
                inputConnection.deleteSurroundingText(1, 0)
                // Insert the new character
                inputConnection.commitText(nextCharacter, 1)
                return
            }
        }
    }
}