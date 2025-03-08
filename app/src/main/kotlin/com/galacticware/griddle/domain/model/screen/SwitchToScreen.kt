package com.galacticware.griddle.domain.model.screen

import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.view.composable.nestedappscreen.AutoFixersScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.BuildYourOwnKeyboardScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.ClipboardScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.EmojiScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.GlobalSettingScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.LanguageSelectionScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.TextReplacementEditorScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.UserSettingsScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.WordPredictionScreen

enum class SwitchToScreen(
    val prettyName: String,
    val screenObject: NestedAppScreen,
    val appSymbol: AppSymbol,
) {
    AUTO_FIXERS("Auto-Fixers", AutoFixersScreen, AppSymbol.AUTO_FIXERS),
    BUILD_YOUR_OWN_KEYBOARD("Build-Your-Own-Keyboard", BuildYourOwnKeyboardScreen, AppSymbol.BUILD_YOUR_OWN_KEYBOARD),
    CLIPBOARD("Clipboard", ClipboardScreen, AppSymbol.TOGGLE_CLIPBOARD),
    EMOJI("Emojis", EmojiScreen, AppSymbol.EMOJI),
    LANGUAGE_PREFERENCES("Language Selection", LanguageSelectionScreen, AppSymbol.LANGUAGE_PREFERENCES),
    TEXT_REPLACEMENT_EDITOR("TextReplacement Editor", TextReplacementEditorScreen, AppSymbol.TEXT_REPLACEMENT_EDITOR, ),
    GLOBAL_SETTINGS("Global Settings", GlobalSettingScreen, AppSymbol.GLOBAL_SETTINGS),
    USER_CHANGEABLE_SETTINGS("User-Changeable Settings", UserSettingsScreen, AppSymbol.USER_CHANGEABLE_SETTINGS),
    WORD_PREDICTION("Word Prediction", WordPredictionScreen, AppSymbol.WORD_PREDICTION),;
}