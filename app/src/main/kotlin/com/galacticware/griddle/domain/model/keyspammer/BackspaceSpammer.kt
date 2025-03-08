package com.galacticware.griddle.domain.model.keyspammer

import android.view.KeyEvent

abstract class BackspaceSpammer: KeySpammer() {
    companion object {
        val backspaceDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
        val controlBackspaceDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
        val backspaceUp = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
    }
}