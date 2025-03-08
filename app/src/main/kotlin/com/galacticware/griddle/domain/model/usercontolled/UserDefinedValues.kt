package com.galacticware.griddle.domain.model.usercontolled

import android.content.Context
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.hapticfeedback.UserVibration
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.util.PreferencesHelper

/**
 * All values that can be used to modify how the keyboard operates should be stored in the
 * [GlobalVarsData], regardless of whether or not there is a visual setting that can be adjusted in
 * the settings menus.
 * TODO, there are a bunch of values right now that could potentially make their way into this
 *  object, which are used in KeyboardView.kt.
 */
data class UserDefinedValues(
    val userHandedness: UserHandedness,
    val minimumDragLength: Int,
    var userVibration: UserVibration,
    var redactionEnabled: Boolean,
    val minimumHoldTime: Int,
    val isRedactionEnabled: Boolean,
    val baseBackspaceSpamSpeed: Double,
    val languageTagData: LanguageTag.UserLanguageData,
    val isGestureTracingEnabled: GestureTracingChoice,
    val turboModeChoice: TurboModeChoice,
) {
    companion object {
        lateinit var current: UserDefinedValues
        fun initializeCurrent(context: Context) {
            current = userDefinedValues(context)
        }

        private fun isRedactionEnabled(context: Context): Boolean = PreferencesHelper.getRedactionEnabled(context)
        private fun minimumHoldTime(context: Context): Int = PreferencesHelper.getMinimumHoldTime(context)
        private fun minimumDragLength(context: Context): Int = PreferencesHelper.getMinimumDragLength(context)
        private fun isUserVibrationPreferred(context: Context): VibrationChoice = PreferencesHelper.getUserVibrationChoice(context)
        private fun isTurboModeEnabled(context: Context): TurboModeChoice = PreferencesHelper.getTurboModeChoice(context)
        private fun userVibrationIntensity(context: Context): Int = PreferencesHelper.getUserVibrationAmplitude(context)
        private fun redactionEnabled(context: Context): Boolean = PreferencesHelper.getRedactionEnabled(context)
        private fun baseBackspaceSpamSpeed(context: Context): Double = PreferencesHelper.getBaseBackspaceSpamSpeed(context)
        private fun isGestureTracingEnabled(context: Context): GestureTracingChoice = PreferencesHelper.getGestureTracingChoice(context)

        private fun primaryLanguage(context: Context): LanguageTag = PreferencesHelper.getUserPrimaryLanguage(context)
        private fun allLanguages(context: Context): Set<LanguageTag> = PreferencesHelper.getUserPreferredLanguages(context)
        private fun currentLanguage(context: Context): LanguageTag = primaryLanguage(context)

        fun currentData(context: Context): UserDefinedValues {
            current = userDefinedValues(context)
            return current
        }

        private fun userDefinedValues(context: Context) = UserDefinedValues(
            redactionEnabled = redactionEnabled(context),
            userHandedness = Keyboard.userHandedness,
            userVibration = UserVibration(
                userVibrationIntensity(context),
                isUserVibrationPreferred(context)
            ),
            minimumDragLength = minimumDragLength(context),
            minimumHoldTime = minimumHoldTime(context),
            isRedactionEnabled = isRedactionEnabled(context),
            baseBackspaceSpamSpeed = baseBackspaceSpamSpeed(context),
            languageTagData = LanguageTag.UserLanguageData(
                primaryLanguage(context),
                allLanguages(context),
                currentLanguage(context)
            ),
            isGestureTracingEnabled = isGestureTracingEnabled(context),
            turboModeChoice = isTurboModeEnabled(context),
        )
    }
}