package com.galacticware.griddle.domain.view.composable.nestedappscreen

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.galacticware.griddle.domain.model.geometry.BoardEdge
import kotlinx.serialization.Serializable
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.screen.NestedAppScreen


@Serializable
object WordPredictionScreen: NestedAppScreen() {
    override val displayNextToKeyboardEdge = BoardEdge.TOP
    private val PREDICTIONS_ROW_HEIGHT = 40.dp
    var textBeforeCursor = ""
    var suggestions = listOf<String>()

    @Composable
    override fun Show() {
        // Display the predictions in a row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            Keyboard.predictions.forEach { wordPrediction ->
                wordPrediction?: return@forEach
                Button(
                    modifier = Modifier.fillMaxWidth()
                    .requiredHeight(20.dp)
                    ,
                    onClick = {
                        val lastSpaceIndex = textBeforeCursor.lastIndexOf(" ")
                            // Can't use negative indices for a wordPrediction, so we have to coerce
                            // it to 0.
                            .coerceAtLeast(0)
                        val connection = keyboardContext.inputConnection

                        val endIndex = connection.selectionBounds()?.first ?: run {
                            // Can't do anything if we can't figure out where the cursor ends.
                            Log.d("WordPredictionScreen", "selectionBounds is null")
                            return@Button
                        }

                        connection.setComposingRegion(lastSpaceIndex, endIndex)

                        // Don't forget to append a space after the word!
                        connection.commitText("$wordPrediction ", 1)

                        // Don't forget to notify the system that we're done composing text!
                        connection.finishComposingText()

                        Log.d(
                            "WordPredictionScreen",
                            "Replaced word to the left of the cursor with $wordPrediction"
                        )

                        // Clear out the predictions after we've used one, since they are no longer valid
                        Keyboard.predictions = mutableListOf()
                    }
                ) {
                    // The content of each composable Button is one of the word predictions
                    Text(text = wordPrediction)
                }
            }
        }
    }

    @Composable
    fun ShowPredictions(newPredictions: List<String>) {
        // Display the predictions in a row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            newPredictions.forEach { wordPrediction ->

                Button(
                    modifier = Modifier.wrapContentWidth()
                        .requiredHeight(PREDICTIONS_ROW_HEIGHT)
                    ,
                    onClick = {
                        val lastSpaceIndex = textBeforeCursor.lastIndexOf(" ")
                            // Can't use negative indices for a wordPrediction, so we have to coerce
                            // it to 0.
                            .coerceAtLeast(0)
                        val connection = keyboardContext.inputConnection

                        val endIndex = connection.selectionBounds()?.first ?: run {
                            // Can't do anything if we can't figure out where the cursor ends.
                            Log.d("WordPredictionScreen", "selectionBounds is null")
                            return@Button
                        }

                        connection.setComposingRegion(lastSpaceIndex, endIndex)

                        // Don't forget to append a space after the word!
                        connection.commitText("$wordPrediction ", 1)

                        // Don't forget to notify the system that we're done composing text!
                        connection.finishComposingText()

                        Log.d(
                            "WordPredictionScreen",
                            "Replaced word to the left of the cursor with $wordPrediction"
                        )

                        // Clear out the predictions after we've used one, since they are no longer valid
                        Keyboard.predictions = mutableListOf()
                    }
                ) {
                    // The content of each composable Button is one of the word predictions
                    Text(text = wordPrediction)
                }
            }
        }
    }
}