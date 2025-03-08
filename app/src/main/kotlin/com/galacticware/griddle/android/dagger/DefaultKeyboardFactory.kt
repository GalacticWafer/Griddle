package com.galacticware.griddle.android.dagger

import android.content.Context
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.keyboard.GriddleEnglishKeyBoardBuilder
import javax.inject.Inject

class DefaultKeyboardFactory @Inject constructor() : KeyboardFactory {
    override fun createKeyboard(context: Context): Keyboard {
        val loadKeyboard = Keyboard.loadKeyboard(context)
        return loadKeyboard ?: createDefaultKeyboard(context)
    }

    private fun createDefaultKeyboard(context: Context): Keyboard {
        return GriddleEnglishKeyBoardBuilder.build(context)
    }
}
