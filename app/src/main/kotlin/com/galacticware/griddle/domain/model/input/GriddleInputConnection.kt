package com.galacticware.griddle.domain.model.input

import android.content.Context
import android.os.Bundle
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.CorrectionInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputContentInfo
import android.widget.EditText
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementUndoState
import com.galacticware.griddle.domain.model.searchbar.InvisibleUpdatingEditText
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.currentState
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel

/**
 * This class exists to add functionality for switching which input focus the keyboard input will go
 * to. For example, when the settings menu is opened, the input will go to the search box within the
 * Griddle Settings popup menu instead of to the currently-focused edit text.
 */
class GriddleInputConnection(private val conn: InputConnection, context: Context)
    : InputConnection {

    var etx: EditText = InvisibleUpdatingEditText.newInstance(context)
    // Extension function to safely get selection start and end
    fun selectionBounds(): Pair<Int, Int>? {
        return try {
            getExtractedText(ExtractedTextRequest(), 0)?.let {
                it.startOffset + it.selectionEnd to it.startOffset + it.selectionEnd}
        } catch (e: Exception) {
            null
        }
    }

    fun convertStringToKeyCode(text: String): List<Int> {
        val events = mKeyCharacterMap.getEvents(text.toCharArray())
        return events?.filter { (it != null) && (it.action == KeyEvent.ACTION_DOWN) }
            ?.map { it.keyCode } ?: listOf()
    }

    /**
     * This function is called from PressKey and SendInput. If it was a PressKey
     */
    fun resolveInputRequest(
        keyCode: Int?,
        singleCharString: String?,
        oneShotMetaState: Int = 0,
        respectShift: Boolean,
    ) {
        val metaState = ignoreShiftIfRequested(oneShotMetaState, respectShift)
        val keyEvents = getKeyEventsForPressKeyOperation(keyCode, metaState, singleCharString)
        if (keyEvents.isEmpty() && singleCharString?.isNotEmpty() == true) {
            commitText(singleCharString)
        } else {
            sendEventsToAppEditTextInKeyboard(keyEvents, etx)
        }
        Keyboard.cancelNonRepeatingModifiers()
    }

    private fun getKeyEventsForPressKeyOperation(
        keyCode: Int?,
        metaState: Int,
        singleCharString: String?
    ): List<KeyEvent> {
        val downTime: Long = 0
        val eventTime = System.currentTimeMillis()
        val keyEvents = if (keyCode != null) {
            listOf(
                KeyEvent(
                    downTime,
                    eventTime,
                    KeyEvent.ACTION_DOWN,
                    keyCode,
                    /*repeat = */ 0,
                    metaState
                )
            )
        } else {
            (mKeyCharacterMap.getEvents((singleCharString ?: "").toCharArray())
                ?: arrayOf<KeyEvent?>()).mapNotNull {
                KeyEvent(
                    downTime,
                    eventTime,
                    it.action,
                    it.keyCode,
                    it.repeatCount,
                    it.metaState or metaState
                )
            }
        }
        return keyEvents
    }

    /**
     * Redact the shift meta state for this key press to ignore the current shift state if
     * not respecting = false.
     * @return the corrected meta state if not respecting shift. otherwise, the original value.
     */
    private fun ignoreShiftIfRequested(
        oneShotMetaState: Int,
        respectShift: Boolean,
    ): Int = if(!respectShift) {
        oneShotMetaState and KeyEvent.META_SHIFT_MASK.inv()
    } else {
        oneShotMetaState
    }

    val editText = InvisibleUpdatingEditText.newInstance(context)

    override fun getTextBeforeCursor(n: Int, flags: Int) = conn.getTextBeforeCursor(n, flags)

    override fun getTextAfterCursor(n: Int, flags: Int) = conn.getTextBeforeCursor(n, flags)

    override fun getSelectedText(flags: Int) = conn.getSelectedText(flags)

    override fun getCursorCapsMode(reqModes: Int) = conn.getCursorCapsMode(reqModes)

    override fun getExtractedText(request: ExtractedTextRequest?, flags: Int) =
        conn.getExtractedText(request, flags)

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int) =
        conn.deleteSurroundingText(beforeLength, afterLength)

    override fun deleteSurroundingTextInCodePoints(beforeLength: Int, afterLength: Int) =
        deleteSurroundingText(beforeLength, afterLength)

    override fun setComposingText(text: CharSequence?, newCursorPosition: Int) =
        conn.setComposingText(text, newCursorPosition)

    override fun setComposingRegion(start: Int, end: Int) = conn.setComposingRegion(start, end)

    override fun finishComposingText() = conn.finishComposingText()

    override fun commitText(
        text: CharSequence?,
        newCursorPosition: Int
    ): Boolean = conn.commitText(text, newCursorPosition)

    /**
     * Insert a string into either the active IME connection as usual,
     * or an editor nested in the UI of the keyboard itself.
     * Then cancel any non-repeating modifiers.
     */
    fun commitText(text: String): Boolean {
        when (inputFocus) {
            AppInputFocus.DEFAULT -> conn.commitText(text, 1)
            else -> insertStringAtCursorPosition(etx, text)
        }
        finishGriddleInput()
        return true
    }

    private fun KeyEvent.toCharList(modifierMask: Int): List<Char> {
        val chars = mutableListOf<Char>()
        if (unicodeChar != 0) {
            chars.add(
                unicodeChar.toChar().let {
                    if ((modifierMask and KeyEvent.META_SHIFT_ON) != 0)
                        it.uppercaseChar()
                    else
                        it.lowercaseChar()
                }
            )
        }
        return chars
    }

    override fun commitCompletion(text: CompletionInfo?) = conn.commitCompletion(text)

    override fun commitCorrection(correctionInfo: CorrectionInfo?) =
        conn.commitCorrection(correctionInfo)

    override fun setSelection(start: Int, end: Int): Boolean {
        currentState = TextReplacementUndoState.NONE
        return conn.setSelection(start, end)
    }
    override fun performEditorAction(editorAction: Int) = conn.performEditorAction(editorAction)

    override fun performContextMenuAction(id: Int) = conn.performContextMenuAction(id)

    override fun beginBatchEdit() = conn.beginBatchEdit()

    override fun endBatchEdit() = conn.endBatchEdit()

    override fun sendKeyEvent(keyEvent: KeyEvent) = conn.sendKeyEvent(keyEvent)

    fun sendRemappedSymbol(
        event: KeyEvent?,
    ): Boolean = run {
        return conn.sendKeyEvent(event)
    }

    fun pressKey(
        eventPrototype: KeyEvent,
        modifiers: Set<ModifierKeyKind>? = null,
    ): Boolean = run  {
        // Calculate the metaState from the modifiers if provided
        val metaState = modifiers?.fold(0) { currentMetaState, modifier ->
            currentMetaState or when (modifier) {
                ModifierKeyKind.SHIFT -> KeyEvent.META_SHIFT_ON
                ModifierKeyKind.CONTROL -> KeyEvent.META_CTRL_ON
                ModifierKeyKind.ALT -> KeyEvent.META_ALT_ON
            }
        } ?: 0
        sendKeyEvent(
            KeyEvent(
                eventPrototype.downTime,
                eventPrototype.downTime,
                eventPrototype.action,
                eventPrototype.keyCode,
                0,
                metaState
            )
        )
    }

    private fun finishGriddleInput() {
        Keyboard.cancelNonRepeatingModifiers()
    }

    override fun clearMetaKeyStates(states: Int) = conn.clearMetaKeyStates(states)
    override fun reportFullscreenMode(enabled: Boolean) = conn.reportFullscreenMode(enabled)
    override fun performPrivateCommand(action: String?, data: Bundle?) =
        conn.performPrivateCommand(action, data)

    override fun requestCursorUpdates(cursorUpdateMode: Int) =
        conn.requestCursorUpdates(cursorUpdateMode)

    override fun getHandler() = conn.handler

    override fun closeConnection() = conn.closeConnection()

    override fun commitContent(inputContentInfo: InputContentInfo, flags: Int, opts: Bundle?) =
        conn.commitContent(inputContentInfo, flags, opts)

    private fun sendEventsToAppEditTextInKeyboard(keyEvents: List<KeyEvent>, etx: EditText) {
        if (inputFocus == AppInputFocus.DEFAULT)
            keyEvents.forEach { sendKeyEvent(it) }
        else {
            keyEvents.forEach {
                etx.dispatchKeyEvent(it)
                etx.dispatchKeyEvent(
                    KeyEvent(
                        it.downTime,
                        it.eventTime,
                        KeyEvent.ACTION_UP,
                        it.keyCode,
                        it.repeatCount,
                        it.metaState,
                    )
                )
            }
            BuildYourOwnKeyboardViewModel.editableInputCallbackMap[inputFocus]!!.updateText(etx.text.toString())
        }
    }

    fun insertStringAtCursorPosition(editText: EditText?, stringToInsert: String) {
        if (editText == null) return
        val editable = editText.text
        editable.insert(editText.selectionStart, stringToInsert)
    }


    fun isPasswordField(editorInfo: EditorInfo?): Boolean {
        return editorInfo?.inputType?.let { inputType ->
            inputType and EditorInfo.TYPE_MASK_CLASS == EditorInfo.TYPE_CLASS_TEXT &&
                    inputType and EditorInfo.TYPE_MASK_VARIATION == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        } ?: false
    }

    companion object {
        val mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
        var inputFocus: AppInputFocus = AppInputFocus.DEFAULT
    }
}
