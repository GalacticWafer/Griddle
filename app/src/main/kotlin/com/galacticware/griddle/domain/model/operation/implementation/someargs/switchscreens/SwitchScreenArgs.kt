package com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens

import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import com.galacticware.griddle.domain.model.screen.SwitchToScreen
import kotlinx.serialization.Serializable

@Serializable
data class SwitchScreenArgs(
    val userSwitchToScreen: SwitchToScreen,
): OperationArgs() {
    override fun description(): String = "Switch to layer ${userSwitchToScreen.prettyName}"
    override fun opInstance(): ParameterizedOperation<*> = SwitchScreens
    companion object {
        val OpenAutoFixers by lazy { SwitchScreenArgs(SwitchToScreen.AUTO_FIXERS) }
        val OpenKeyboardDesigner by lazy { SwitchScreenArgs(SwitchToScreen.BUILD_YOUR_OWN_KEYBOARD) }
        val OpenClipboard by lazy { SwitchScreenArgs(SwitchToScreen.CLIPBOARD) }
        val OpenEmoji by lazy { SwitchScreenArgs(SwitchToScreen.EMOJI) }
        val OpenLanguagePreferences by lazy { SwitchScreenArgs(SwitchToScreen.LANGUAGE_PREFERENCES) }
        val OpenTextReplacementEditor by lazy { SwitchScreenArgs(SwitchToScreen.TEXT_REPLACEMENT_EDITOR) }
        val OpenBaseSettings by lazy { SwitchScreenArgs(SwitchToScreen.GLOBAL_SETTINGS) }
        val OpenUserSettings by lazy { SwitchScreenArgs(SwitchToScreen.USER_CHANGEABLE_SETTINGS) }
        val OpenWordPrediction by lazy { SwitchScreenArgs(SwitchToScreen.WORD_PREDICTION) }
        val OpenGriddleSetting = SwitchScreenArgs(SwitchToScreen.USER_CHANGEABLE_SETTINGS)

        val instances by lazy {
            setOf(OpenAutoFixers, OpenKeyboardDesigner, OpenClipboard, OpenEmoji, OpenLanguagePreferences,
                OpenTextReplacementEditor, OpenBaseSettings, OpenUserSettings, OpenWordPrediction)
        }
    }
}