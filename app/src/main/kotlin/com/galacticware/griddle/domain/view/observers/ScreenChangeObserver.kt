package com.galacticware.griddle.domain.view.observers

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.galacticware.griddle.domain.model.textreplacement.CheckUndo.Companion.currentState
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementUndoState

class ScreenChangeObserver : DefaultLifecycleObserver {
    override fun onPause(owner: LifecycleOwner) {
        currentState = TextReplacementUndoState.NONE
    }

    override fun onResume(owner: LifecycleOwner) {
        currentState = TextReplacementUndoState.NONE
    }
}