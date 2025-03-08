package com.galacticware.griddle.domain.model.keyboard.definition.designs.base

import android.content.Context
import com.galacticware.griddle.domain.model.keyboard.Keyboard

interface KeyboardBuilder {
    fun build(context: Context): Keyboard
}