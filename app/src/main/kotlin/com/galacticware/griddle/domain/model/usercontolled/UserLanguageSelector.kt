package com.galacticware.griddle.domain.model.usercontolled

import android.content.Context
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.util.PreferencesHelper

object UserLanguageSelector {
    fun setUserPreferredLanguages(context: Context, languageTags: Set<LanguageTag> = incomingLanguageLayerUpdate.first.filterNotNull().toSet()) {
        PreferencesHelper.setAllLanguages(context, languageTags)
    }
    var incomingLanguageLayerUpdate: Pair<List<LanguageTag?>, Context?> = emptyList<LanguageTag?>() to null as Context?
        set(value) {
            field = value
            value.second?.let { context ->
                setUserPreferredLanguages(context)
            }

        }
    var primaryLanguageTag: LanguageTag = LanguageTag.ENGLISH
    var currentLanguages: List<LanguageTag?> = mutableListOf(primaryLanguageTag)
    var languageTags: MutableSet<LanguageTag> = mutableSetOf()
}
