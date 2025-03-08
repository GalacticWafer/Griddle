package com.galacticware.griddle.domain.model.textreplacement


/**
 * This class is used to check if the user has entered a macro abbreviation, and if so, replace it with the
 * macro's replacement. It also checks if the user has pressed backspace, and if so, replaces the macro's
 * replacement with the abbreviation.
 */
open class CheckUndo {
    companion object {
        var currentState: TextReplacementUndoState = TextReplacementUndoState.NONE
        var savedTextReplacement: TextReplacement? = null
    }
}