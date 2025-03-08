package com.galacticware.griddle.domain.model.textreplacement

import android.content.Context


sealed interface TextReplacementEvent {
    data object AddTextReplacement : TextReplacementEvent
    data object SaveTextReplacement : TextReplacementEvent
    data object HideDeletionConfirmation : TextReplacementEvent
    data object CancelSavingTextReplacement: TextReplacementEvent
    data object ExportTextReplacements: TextReplacementEvent
    data object ImportTextReplacements: TextReplacementEvent

    data class SetRequiredWhiteSpace(val requiresWhitespaceBefore: Boolean) : TextReplacementEvent
    data class DeleteTextReplacement(val context: Context) : TextReplacementEvent
    data class ShowDeletionConfirmation(val abbreviation: String) : TextReplacementEvent
    data class SetAbbreviation(val abbreviation: String): TextReplacementEvent
    data class SetReplacement(val replacement: String): TextReplacementEvent
    data class SortTextReplacements(val sortType: TextReplacementListSortType) : TextReplacementEvent
    data class UpdateSearchQuery(val query: String) : TextReplacementEvent
}