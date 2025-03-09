package com.galacticware.griddle.domain.model.input

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.view.KeyboardView
import com.galacticware.griddle.domain.view.composable.nestedappscreen.WordPredictionScreen

class IMEService : LifecycleInputMethodService(),
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    override fun onShowInputRequested(flags: Int, configChange: Boolean): Boolean {
        KeyboardView.isVisible = true
        return super.onShowInputRequested(flags, configChange)
    }

    override fun onCreateInputView(): View {
//        isVisible = true
        val view = KeyboardView(this)

        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
        return view
    }

    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int,
        newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int
    ) {
//        isVisible = true
        super.onUpdateSelection(
            oldSelStart, oldSelEnd, newSelStart, newSelEnd,
            candidatesStart, candidatesEnd
        )
        // Your logic to run when the cursor position changes
        // For example, you can call a function here
        currentInputConnection?.getExtractedText(ExtractedTextRequest(), 0)?.let { extractedText ->
            val (selectionStart, selectionEnd) = extractedText.let { it.selectionStart to it.selectionEnd }
            if(selectionStart == selectionEnd && Keyboard.shiftState == ModifierKeyState.OFF) {
                TextSelectionAnchor.currentPosition = null
            }
            if (Keyboard.wasLastActionBackspace) {
                return
            }
            extractedText.text?.let { text ->
                if (selectionStart == selectionEnd) {
                    val stringBeforeCaret = text.substring(0 until extractedText.selectionStart)
                    var didChangePunctuation = false
                    var didChangeCaps = false
                    val prefersAutoCapitalization =
                        PreferencesHelper.getAutoCapitalizationPreference(applicationContext)
                    val prefersAutoCorrection =
                        PreferencesHelper.getAutoCorrectionPreference(applicationContext)
                    val prefersAutoPunctuation =
                        PreferencesHelper.getAutoPunctuationPreference(applicationContext)
                    if (stringBeforeCaret.matches(".\\s".toRegex())
                        && prefersAutoCapitalization
                    ) {
                        Keyboard.didLastActionAutoCapitalize = false
                    } else if (stringBeforeCaret.endsWith(". ")
                        && prefersAutoCapitalization && !Keyboard.didLastActionAutoCapitalize) {
                        Keyboard.cycleToOneShotStateFor(ModifierKeyKind.SHIFT)
                        Keyboard.didLastActionAutoCapitalize = true
                        didChangeCaps = true
                    } else if (("\\s\\s$".toRegex().containsMatchIn(stringBeforeCaret))
                        && prefersAutoPunctuation && !Keyboard.didLastActionAutoPuctuate) {
                        // replace the last space with a period
                        if (stringBeforeCaret.length > 1
                            && stringBeforeCaret.trim().last() !in setOf('.', ',', '?')) {
                            currentInputConnection.deleteSurroundingText(2, 0)
                            currentInputConnection?.commitText(". ", 1)
                            Keyboard.didLastActionAutoPuctuate = true
                            didChangePunctuation = true
                            if (prefersAutoCapitalization && !Keyboard.didLastActionAutoCapitalize) {
                                Keyboard.cycleToOneShotStateFor(ModifierKeyKind.SHIFT)
                                Keyboard.didLastActionAutoCapitalize = true
                                didChangeCaps = true
                            }
                        }
                    }
                    if (!didChangePunctuation) {
                        Keyboard.didLastActionAutoPuctuate = false
                    }

                    if (!didChangeCaps) {
                        Keyboard.didLastActionAutoCapitalize = false
                    }
                }

                val textBeforeCursor = extractedText.text?.substring(0 until extractedText.selectionStart)
                    ?: run {
                        // Can't do anything if we can't figure out where the cursor ends.
                        Log.d("WordPredictionScreen", "selectionBounds is null")
                        return
                    }
                if (textBeforeCursor != WordPredictionScreen.textBeforeCursor) {
                  /*  WordPredictionScreen.textBeforeCursor = textBeforeCursor
                    nextWordPredictor?.predict(textBeforeCursor)?.let { result ->
                        Keyboard.predictions = result.predictions
                            .map { wordPrediction ->
                                wordPrediction.predictionToDisplay
                            }
                    }*/
                }
            }
        }?: run {
            TextSelectionAnchor.currentPosition = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)

    }

    override val viewModelStore: ViewModelStore
        get() = store
    override val lifecycle: Lifecycle
        get() = dispatcher.lifecycle


    //ViewModelStore Methods
    private val store = ViewModelStore()

    //SaveStateRegestry Methods

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    companion object {
        fun hideKeyboard(context: Context) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = (context as Activity).currentFocus
            if (view != null) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        /*@SuppressLint("StaticFieldLeak")
        var nextWordPredictor: WordPredictor? = null*/
    }
}


/*
class IMEService : LifecycleInputMethodService(), ViewModelStoreOwner, SavedStateRegistryOwner {

    private lateinit var textPredictor: TextPredictor

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)

        // Initialize the TextPredictor with a callback to handle suggestions
        textPredictor = TextPredictor(this) { suggestions ->
            // Handle UI changes that display `suggestions`
            WordPredictionScreen.predictions = suggestions.toMutableList()
            Log.d("WordPredictionScreen", "Predicted words: $suggestions")
        }
    }

    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int,
        newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart, oldSelEnd, newSelStart, newSelEnd,
            candidatesStart, candidatesEnd
        )

        currentInputConnection?.getExtractedText(ExtractedTextRequest(), 0)?.let { extractedText ->
            if (Keyboard.wasLastActionBackspace) {
                return
            }
            val (selectionStart, selectionEnd) = extractedText.let { it.selectionStart to it.selectionEnd }
            extractedText.text?.let { text ->
                if (selectionStart == selectionEnd) {
                    val textBeforeCursor = text.substring(0 until extractedText.selectionStart)
                    if (textBeforeCursor != WordPredictionScreen.textBeforeCursor) {
                        WordPredictionScreen.textBeforeCursor = textBeforeCursor
                        textPredictor.stream(textBeforeCursor)
                    }
                }
            }
        }
    }

    // Other methods and properties...

    override val viewModelStore: ViewModelStore
        get() = store
    override val lifecycle: Lifecycle
        get() = dispatcher.lifecycle

    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    companion object {
        @SuppressLint("StaticFieldLeak")
        var nextWordPredictor: NextWordPredictor? = null
    }
}
 */