package com.galacticware.griddle.domain.model.textreplacement

import android.content.Context
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.util.PreferencesHelper

val DEFAULT_TEXT_REPLACEMENTS = listOf(
    ":(" to "😢",
    ":P" to "😛",
    ":D" to "😄",
    ":cry:" to "😢",
).map { Triple(it.first, it.second, true) }.toSet()
    .plus (
        setOf(
            // auto-combiners, they don't require a space to trigger
            ":ae" to "æ",
            ":AE" to "Æ",
            ":oe" to "œ",
            ":OE" to "Œ",
            ":ss" to "ß",
            ":th" to "þ",
            ":TH" to "Þ",
            ":dh" to "ð",
            ":DH" to "Ð",
            ":?!" to "‽",
            ":!?" to "‽",
        ).map {
            Triple(it.first, it.second, false)
        }
    )
    .map { TextReplacement(it.first, it.second, it.third) }


@OptIn(ExperimentalCoroutinesApi::class)
class TextReplacementViewModel(
    private val dao: TextReplacementDao,
    context: Context,
) : ViewModel() {

    init {
        if (PreferencesHelper.isFirstRun(context)) {
            PreferencesHelper.setDefaultTextReplacements(context, DEFAULT_TEXT_REPLACEMENTS.associateWith { /*isDeleted*/ false })
            PreferencesHelper.setFirstRun(context, false)
        }

        (PreferencesHelper.getDefaultTextReplacements(context) ?: emptySet()).forEach{
            viewModelScope.launch {
                dao.upsertTextReplacement(it)
            }
        }

        viewModelScope.launch {
            val l = mutableListOf<TextReplacement>()
            dao.getAllTextReplacements().collect { list ->
                l.addAll(list)
                Keyboard.textReplacements = l.toSet().toList()
            }
        }
    }

    private val _sortType = MutableStateFlow(TextReplacementListSortType.ABBREVIATION)

    private val _text_replacements = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                TextReplacementListSortType.ABBREVIATION -> dao.getTextReplacementsByAbbreviation()
                TextReplacementListSortType.REPLACEMENT -> dao.getTextReplacementsByReplacement()
                TextReplacementListSortType.WHITESPACE_INSENSITIVE -> dao.getAllTextReplacementsByWhiteSpaceRequirement()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DEFAULT_TEXT_REPLACEMENTS)

    private val _state = MutableStateFlow(TextReplacementState())

    val state = combine(_state, _sortType, _text_replacements) { state, sortType, textReplacements ->
        state.copy(
            textReplacements = textReplacements,
            sortType = sortType,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5),
        TextReplacementState(),
    )

    fun onEvent(event: TextReplacementEvent) {
        when (event) {
            is TextReplacementEvent.HideDeletionConfirmation -> {
                _state.update {
                    it.copy(
                        isAddingTextReplacement = null,
                    )
                }
            }

            is TextReplacementEvent.SaveTextReplacement -> {
                if (_state.value.abbreviation.isBlank() ||
                    _state.value.replacement.isBlank()) {
                    return
                }

                viewModelScope.launch {
                    val textReplacement = TextReplacement(
                        abbreviation = _state.value.abbreviation,
                        replacement = _state.value.replacement,
                        requiresWhitespaceBefore = _state.value.requiresWhitespaceBefore,
                    )
                    Keyboard.textReplacements.firstOrNull { it.abbreviation == _state.value.abbreviation }
                        ?.let {
                            dao.deleteTextReplacement(it)
                        }
                    dao.upsertTextReplacement(textReplacement)
                    reloadTextReplacements()
                }

                _state.update {
                    it.copy(
                        isAddingTextReplacement = null,
                        abbreviation = "",
                        replacement = "",
                    )
                }
            }

            is TextReplacementEvent.SetAbbreviation -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            abbreviation = event.abbreviation,
                        )
                    }
                    reloadTextReplacements()
                }
            }

            is TextReplacementEvent.SetRequiredWhiteSpace -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            requiresWhitespaceBefore = event.requiresWhitespaceBefore,
                        )
                    }
                    reloadTextReplacements()
                }
            }

            is TextReplacementEvent.SetReplacement -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            replacement = event.replacement,
                        )
                    }
                    reloadTextReplacements()
                }
            }

            is TextReplacementEvent.ShowDeletionConfirmation -> {
                _state.update {
                    it.copy(
                        abbreviation = event.abbreviation,
                        isAddingTextReplacement = TextReplacementListOperation.DELETE,
                    )
                }
            }

            is TextReplacementEvent.SortTextReplacements -> {
                _sortType.value = event.sortType
            }

            is TextReplacementEvent.DeleteTextReplacement -> {
                viewModelScope.launch {
                    for (textReplacement in Keyboard.textReplacements) {
                        if (textReplacement.abbreviation == _state.value.abbreviation) {
                            dao.deleteTextReplacement(textReplacement)
                            // if it's a default textReplacement, change it to isDeleted = true in shared prefs
                            val defaultTextReplacements = (PreferencesHelper.getDefaultTextReplacements(event.context) ?: emptySet())
                                .filter { it.abbreviation != textReplacement.abbreviation }
                            PreferencesHelper.setDefaultTextReplacements(event.context, defaultTextReplacements.associateWith {
                                it.abbreviation == textReplacement.abbreviation
                            })
                            break
                        }
                    }
                    reloadTextReplacements()
                }
                _state.update {
                    it.copy(
                        isAddingTextReplacement = null,
                        abbreviation = "",
                        replacement = "",
                        requiresWhitespaceBefore = true,
                    )
                }
            }

            is TextReplacementEvent.AddTextReplacement -> {
                _state.update { it.copy(isAddingTextReplacement = TextReplacementListOperation.SAVE) }
            }

            is TextReplacementEvent.UpdateSearchQuery -> {
                viewModelScope.launch {
                    _state.update { it.copy(searchQuery = event.query) }
                    reloadTextReplacements()
                }
            }

            is TextReplacementEvent.CancelSavingTextReplacement -> {
                _state.update {
                    it.copy(
                        isAddingTextReplacement = null,
                        abbreviation = "",
                        replacement = "",
                    )
                }
            }

            is TextReplacementEvent.ExportTextReplacements -> {
                /*viewModelScope.launch {
                    val textReplacements = Keyboard.textReplacements
                }*/
            }

            TextReplacementEvent.ImportTextReplacements -> {}
        }
    }

    suspend fun reloadTextReplacements() {
        dao.getAllTextReplacements().collect { list ->
            Keyboard.textReplacements = list
                .toSet() // remove duplicates, even though there shouldn't be any...
                .toList()
                .also { l ->
                    if(l.isEmpty()) {
                        return@collect
                    }
                    val (l1, l2) =  setOf(
                        l.filter {
                            thing -> thing.requiresWhitespaceBefore
                                 },
                            l.filter {
                                thing -> !thing.requiresWhitespaceBefore
                            }
                    ).map { ll ->
                        ll.sortedBy {
                            when (_sortType.value) {
                                TextReplacementListSortType.ABBREVIATION, TextReplacementListSortType.WHITESPACE_INSENSITIVE -> it.abbreviation
                                TextReplacementListSortType.REPLACEMENT -> it.replacement
                            }
                        }
                    }
                    if(_sortType.value == TextReplacementListSortType.WHITESPACE_INSENSITIVE) {
                        l1 + l2
                    } else {
                        l1 + l2
                    }
                }
        }
    }
}

