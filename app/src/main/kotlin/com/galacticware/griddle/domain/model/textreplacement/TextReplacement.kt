package com.galacticware.griddle.domain.model.textreplacement

import android.os.Build
import android.view.inputmethod.ExtractedTextRequest
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.input.GriddleInputConnection
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementUndoState.APPLIED
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementUndoState.NONE
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.DeleteLeft
import com.galacticware.griddle.domain.model.operation.implementation.noargs.noop.NoOp
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKey
import com.galacticware.griddle.domain.model.operation.base.SavedExecution
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.currentState
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.savedTextReplacement
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues

@Entity(tableName = "textReplacements")
data class TextReplacement(
    val abbreviation: String,
    val replacement: String,
    val requiresWhitespaceBefore: Boolean,
    @PrimaryKey(autoGenerate = false) val id: Int = abbreviation.hashCode(),
) {
    companion object {
        private fun tryTextReplacementExpansion(keyboardContext: KeyboardContext): Boolean {
            val charsToInsert: String = keyboardContext.currentText
            val conn = keyboardContext.inputConnection

            // Get cursor position
            val (selectionStart, selectionEnd) = conn.selectionBounds() ?: return false
            if (selectionStart != selectionEnd) return false

            // Get the current text and cursor position
            val extractedText = conn.getExtractedText(ExtractedTextRequest(), 0)
            val inputText = extractedText?.text?.substring(0, selectionEnd) ?: return false

            if (tryRegexExpansion(inputText, selectionStart, charsToInsert, conn, requiresWhitespaceBefore = true)) return true
            if(tryRegexExpansion(inputText, selectionStart, charsToInsert, conn, requiresWhitespaceBefore = false)) return true

            return false
        }

        private fun tryRegexExpansion(
            inputText: String,
            selectionStart: Int,
            charsToInsert: String,
            conn: GriddleInputConnection,
            requiresWhitespaceBefore: Boolean
        ): Boolean {
            // Regex to find matches
            val regex = if(requiresWhitespaceBefore) """${"(^|\\s)"}(\S+)""".toRegex()
            else """(\S+)""".toRegex()
            val matches = regex.findAll(inputText.substring(0, selectionStart))

            // Check for overlapping matches
            for (match in matches) {
                val matchRange = match.range
                if (matchRange.last == selectionStart - 1 && matchRange.first >= 0) {
                    val matchedText =
                        match.value.trim() // Get the matched text without leading whitespace

                    // Check against textReplacements
                    for (textReplacement in Keyboard.textReplacements.filter {
                        if(requiresWhitespaceBefore) it.requiresWhitespaceBefore else !it.requiresWhitespaceBefore
                    }) {
                        val isMatchWithWhitespace = requiresWhitespaceBefore
                                    && matchedText == textReplacement.abbreviation.dropLast(1)
                                    && textReplacement.abbreviation.substring(matchedText.length)
                                        .endsWith(charsToInsert)

                        val isMatchWithoutWhitespace = !requiresWhitespaceBefore
                                && textReplacement.abbreviation.endsWith(charsToInsert)

                        val isMatch = isMatchWithWhitespace || isMatchWithoutWhitespace

                        if (matchedText.endsWith(textReplacement.abbreviation.dropLast(1)) && isMatch) {
                            // Replace the matched text with the textReplacement replacement
                            val start = matchRange.first + matchedText.length - textReplacement.abbreviation.dropLast(1).length
                            val end = selectionStart // Replace up to cursor position
                            conn.setComposingRegion(0, selectionStart)
                            val newText = "${inputText.substring(0, start)}${
                                if (start == 0 || !requiresWhitespaceBefore) "" else " "
                            }${textReplacement.replacement}${inputText.substring(end)}"
                            conn.deleteSurroundingText(selectionStart, 0)
                            conn.setComposingText(newText, 1)
                            conn.finishComposingText()

                            currentState = APPLIED
                            savedTextReplacement = textReplacement
                            return true
                        }
                    }
                }
            }
            return false
        }

        fun checkForReplacementAndThen(keyboardContext: KeyboardContext, operation: Operation) {
            savedTextReplacement
                ?.let { tryTextReplacementRedaction(keyboardContext) }
                ?: run {
                    if(!tryTextReplacementExpansion(keyboardContext)) {
                        operation.executeOperation(keyboardContext)
                    }
                }
        }

        fun tryTextReplacementRedaction(keyboardContext: KeyboardContext): Boolean {
            val mostRecentTextReplacement = savedTextReplacement
            savedTextReplacement = null

            val operation = keyboardContext.gesture.currentAssignment.operation
            val isPressedKeyBackspace = operation is PressKey && operation.isBackspace
            val (start, end) = keyboardContext.inputConnection.selectionBounds() ?: return false

            val canDoTextReplacementRedaction = currentState == APPLIED &&
                    mostRecentTextReplacement != null &&
                    (keyboardContext.causesTextReplacementRedaction || isPressedKeyBackspace) &&
                    start == end

            if (!canDoTextReplacementRedaction) {
                // send it off and return
                keyboardContext.gesture.editorOperation.let{
                    if (it is PressKey) {
                        it.executeOperation(keyboardContext)
                        currentState = NONE
                        return true
                    }
                }
            }

            // Check if this is the raw backspace keyEvent
            if (UserDefinedValues.current.isRedactionEnabled) {
                val extractedText = keyboardContext.inputConnection.getExtractedText(ExtractedTextRequest(), 0)
                val text = extractedText?.text?.toString() ?: ""
                val isTextReplacementReplacementImmediatelyToTheLeft = text.substring(
                    maxOf(0, start - mostRecentTextReplacement!!.replacement.length),
                    start
                ) == mostRecentTextReplacement.replacement
                if (!isTextReplacementReplacementImmediatelyToTheLeft) {
                    return false
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    keyboardContext.inputConnection.replaceText(
                        end - mostRecentTextReplacement.replacement.length,
                        end,
                        mostRecentTextReplacement.abbreviation,
                        1,
                        null
                    )
                } else {
                    keyboardContext.inputConnection.setComposingRegion(start, text.length)
                    keyboardContext.inputConnection.setComposingText(
                        mostRecentTextReplacement.replacement,
                        1
                    )
                }
                return true
            } else {
                DeleteLeft.perform(
                    keyboardContext,
                    context = keyboardContext.context,
                    savedExecution = SavedExecution(NoOp){}
                )
                return false
            }
        }
    }
}