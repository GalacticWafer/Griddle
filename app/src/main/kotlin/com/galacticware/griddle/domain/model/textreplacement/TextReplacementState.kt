package com.galacticware.griddle.domain.model.textreplacement

data class TextReplacementState(
    val textReplacements: List<TextReplacement> = emptyList(),
    val abbreviation: String = "",
    val replacement: String = "",
    val requiresWhitespaceBefore: Boolean = true,
    var isAddingTextReplacement: TextReplacementListOperation? = null,
    val sortType: TextReplacementListSortType = TextReplacementListSortType.ABBREVIATION,
    val searchQuery: String = "",
)


