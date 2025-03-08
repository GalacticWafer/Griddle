package com.galacticware.griddle.domain.model.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import com.galacticware.griddle.domain.model.error.VoiceTextError
import com.galacticware.griddle.domain.model.input.GriddleInputConnection
import com.galacticware.griddle.domain.model.input.IMEService
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues

fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val durationMillis = 50L // Adjust the duration as needed

    if (Build.VERSION.SDK_INT >= 26) {
        // Newer Android versions
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                durationMillis,
                UserDefinedValues.current.userVibration.amplitude
            )
        )
    } else {
        // Older Android versions
        vibrator.vibrate(durationMillis)
    }
}

class SpeechRecognitionDelegate(val context: Context) {
    companion object {
        val intent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        private lateinit var recognizer: SpeechRecognizer
        fun setRecognizer(context: Context) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
    }
    private var listener: RecognitionListener
    init {
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            "com.domain.app"
        )

        listener = object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!voiceResults.isNullOrEmpty()) {
                    val inputConnection = GriddleInputConnection(
                        (context as IMEService).currentInputConnection,
                        context
                    )
                    val edit = inputConnection.getExtractedText(
                        android.view.inputmethod.ExtractedTextRequest(), 0
                    )
                    if (edit != null) {
                        inputConnection.commitText(voiceResults[0])
                    }
                }
            }

            override fun onReadyForSpeech(params: Bundle) {
                Toast.makeText(context, "Ready for speech", Toast.LENGTH_SHORT).show()
            }


            override fun onError(errorCode: Int) {
                VoiceTextError.valueFromErrorCode(errorCode).let {
                    Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onBeginningOfSpeech() {
                Toast.makeText(context, "Speech starting", Toast.LENGTH_SHORT).show()
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // TODO Auto-generated method stub
            }

            override fun onEndOfSpeech() {
                // get the text from speech

            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // TODO Auto-generated method stub
            }

            override fun onPartialResults(partialResults: Bundle) {
                // TODO Auto-generated method stub
            }

            override fun onRmsChanged(rmsdB: Float) {
                // TODO Auto-generated method stub
            }
        }
    }
    fun listen() {
        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)
    }
}
