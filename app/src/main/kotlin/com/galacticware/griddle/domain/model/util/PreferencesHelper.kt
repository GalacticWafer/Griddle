package com.galacticware.griddle.domain.model.util

import android.content.Context
import android.content.SharedPreferences
import com.galacticware.griddle.R
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.keyboard.KeyboardOffsetAndSize
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.layer.GriddleEnglishLayerBuilder
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.layer.LayerDefinable
import com.galacticware.griddle.domain.model.textreplacement.TextReplacement
import com.galacticware.griddle.domain.model.usercontolled.GestureTracingChoice
import com.galacticware.griddle.domain.model.usercontolled.GriddleSetting
import com.galacticware.griddle.domain.model.usercontolled.TurboModeChoice
import com.galacticware.griddle.domain.model.usercontolled.VibrationChoice
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_SIZE

object PreferencesHelper {
    const val PREFS_NAME = R.string.user_prefs.toString()
    const val AUTO_CAPS = R.string.auto_caps.toString()
    const val AUTO_PUNCTUATION = R.string.auto_punctuation.toString()
    const val AUTO_CORRECTION = R.string.auto_correct.toString()
    private const val KEY_DEFAULT_TEXT_REPLACEMENTS = R.string.default_text_replacements.toString()
    private const val KEY_FIRST_RUN = R.string.first_run.toString()
    private const val PRIMARY_ALPHA = R.string.primary_alpha_layer.toString()
    private const val REDACTION_ENABLED = R.string.redaction_enabled.toString()
    private const val PRIMARY_LANGUAGE = R.string.primary_language.toString()
    private const val LANGUAGE_PREFERENCES = R.string.language_preferences.toString()
    private const val BOARD_POSITION_X = R.string.board_x.toString()
    private const val BOARD_POSITION_WIDTH = R.string.board_width.toString()
    private const val BOARD_POSITION_HEIGHT = R.string.board_height.toString()
    private const val TURBO_MODE = R.string.turbo_mode.toString()

    private fun checkAndGetUserDefinedValue(
        context: Context,
        setting: GriddleSetting,
        valueSetterLambda: (Int) -> Unit,
    ): Int {
        if (!getPreferences(context).contains(setting.name)) {
            getPreferences(context).edit().putInt(setting.name, setting.defaultValue)
                .apply()
        }
        val int = getPreferences(context).getInt(setting.name, setting.minValue - 1)
        if (int < setting.minValue) {
            setting.minValue.let(valueSetterLambda)
        } else if (int > setting.maxValue) {
            setting.maxValue.let(valueSetterLambda)
        }
        return int
    }

    private fun checkAndSetUserDefinedValue(
        context: Context,
        intValue: Int,
        griddleSetting: GriddleSetting
    ) {
        getPreferences(context).edit().putInt(
            griddleSetting.name,
            intValue.coerceAtMost(griddleSetting.maxValue)
                .coerceAtLeast(griddleSetting.minValue)
        ).apply()
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isFirstRun(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_FIRST_RUN, true)
    }

    fun setFirstRun(context: Context, isFirstRun: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_FIRST_RUN, isFirstRun)
        editor.apply()
    }

    fun setDefaultTextReplacements(context: Context, textReplacementsEntries: Map<TextReplacement, Boolean>) {
        val editor = getPreferences(context).edit()
        editor.putStringSet(KEY_DEFAULT_TEXT_REPLACEMENTS, textReplacementsEntries.map {
            (textReplacement, isDeleted) ->
            "${textReplacement.abbreviation.length}:${textReplacement.replacement.length}:" +
            "${textReplacement.abbreviation}${textReplacement.replacement}=$isDeleted:" +
            if (textReplacement.requiresWhitespaceBefore) "t" else "f"
        }.toSet())
        editor.apply()
    }

    fun getDefaultTextReplacements(context: Context): Set<TextReplacement>? {
        val stringSet = getPreferences(context).getStringSet(KEY_DEFAULT_TEXT_REPLACEMENTS, emptySet())
        return stringSet?.mapNotNull {
            val abbreviationLength = it.substringBefore(':').toInt()
            val replacementLength = it.substringAfter(':').substringBefore(':').toInt()
            val abbreviation = it.substringAfter(':').substringAfter(':').substring(0, abbreviationLength)
            val replacement = it.substringAfter(':').substringAfter(':').substring(abbreviationLength, abbreviationLength + replacementLength)
            val isDeleted = it.substringAfterLast('=') == "true"
            val requiresWhitespaceBefore = it.substringAfterLast(':') == "t"
            if (isDeleted) null else TextReplacement(abbreviation, replacement, requiresWhitespaceBefore)
        }?.toSet()
    }

    fun getPrimaryAlphaLayer(context: Context): LayerDefinable = run {
        try {
            val className = getPreferences(context).getString(PRIMARY_ALPHA, null)
            if(className == null) {
                val layer =
                GriddleEnglishLayerBuilder.build(context)
                setPrimaryAlphaLayer(context, layer)
                getPreferences(context).edit().putString(PRIMARY_ALPHA,
                    layer.name).apply()
                return layer
            }
            val clazz = Class.forName(className)
            val constructor = clazz.constructors.firstOrNull { it.parameterTypes.contains(Context::class.java) }
            if (constructor != null) {
                constructor.newInstance(context) as LayerDefinable
            } else {
                clazz.constructors.first { it.parameterTypes.isEmpty() }.newInstance() as LayerDefinable
            }
        } catch (e: ClassNotFoundException) {
            val layer = GriddleEnglishLayerBuilder.build(context)
            setPrimaryAlphaLayer(context, layer)
            return layer
        }
    }

    fun setPrimaryAlphaLayer(context: Context, layer: LayerDefinable) {
        getPreferences(context).edit().putString(PRIMARY_ALPHA, layer.name)
            .apply()
    }

    fun getMinimumHoldTime(context: Context): Int 
    = checkAndGetUserDefinedValue(context, GriddleSetting.MINIMUM_HOLD_TIME) { 
        t -> setMinimumHoldTime(context, t) 
    }

    fun setMinimumHoldTime(context: Context, time: Int)
    = checkAndSetUserDefinedValue(context, time, GriddleSetting.MINIMUM_HOLD_TIME)

    fun getUserVibrationAmplitude(context: Context): Int
     = checkAndGetUserDefinedValue(context, GriddleSetting.VIBRATION_AMPLITUDE) { 
         t -> setUserVibrationAmplitude(context, t)
     }

    fun setUserVibrationAmplitude(context: Context, amplitude: Int) =
        checkAndSetUserDefinedValue(context, amplitude, GriddleSetting.VIBRATION_AMPLITUDE)

    fun getRedactionEnabled(context: Context): Boolean {
        if(!getPreferences(context).contains(REDACTION_ENABLED)) {
            getPreferences(context).edit().putBoolean(REDACTION_ENABLED, true).apply()
        }
        return getPreferences(context).getBoolean(REDACTION_ENABLED, true)
    }

    fun setRedactionEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit().putBoolean(REDACTION_ENABLED, enabled).apply()
    }

    fun getUserVibrationChoice(context: Context): VibrationChoice
    = VibrationChoice.entries[checkAndGetUserDefinedValue(context, GriddleSetting.IS_VIBRATION_ENABLED) {
        intValue -> setUserVibrationChoice(context, intValue)
    }]

    fun getTurboModeChoice(context: Context): TurboModeChoice
    = TurboModeChoice.entries[checkAndGetUserDefinedValue(context, GriddleSetting.IS_TURBO_ENABLED) {
        intValue -> setUserVibrationChoice(context, intValue)
    }]

    fun setUserVibrationChoice(context: Context, choice: Int)
    = checkAndSetUserDefinedValue(context, choice, GriddleSetting.IS_VIBRATION_ENABLED)


    fun getGestureTracingChoice(context: Context): GestureTracingChoice
    = GestureTracingChoice.entries[checkAndGetUserDefinedValue(context, GriddleSetting.IS_GESTURE_TRACING_ENABLED) {
        intValue -> setUserVibrationChoice(context, intValue)
    }]

    fun setGestureTracingChoice(context: Context, choice: Int)
    = checkAndSetUserDefinedValue(context, choice, GriddleSetting.IS_GESTURE_TRACING_ENABLED)

    fun getMinimumDragLength(context: Context): Int =
        checkAndGetUserDefinedValue(context, GriddleSetting.MINIMUM_DRAG_LENGTH) { 
            intValue -> setMinimumDragLength(context, intValue)
        }

    fun setMinimumDragLength(context: Context, length: Int)
    = checkAndSetUserDefinedValue(context, length, GriddleSetting.MINIMUM_DRAG_LENGTH)

    fun getMinimumCircleRadius(context: Context): Int = 
        checkAndGetUserDefinedValue(context, GriddleSetting.MINIMUM_CIRCLE_RADIUS) { 
            intValue -> setMinimumCircleRadius(context, intValue)
        }

    fun setMinimumCircleRadius(context: Context, intValue: Int) =
        checkAndSetUserDefinedValue(context, intValue, GriddleSetting.MINIMUM_CIRCLE_RADIUS)

    fun getBaseBackspaceSpamSpeed(context: Context): Double =
        checkAndGetUserDefinedValue(context, GriddleSetting.MINIMUM_BACKSPACE_SPAMMING_SPEED) {
                intValue -> setBaseBackspaceSpamSpeed(context, intValue)
        }.toDouble()

    fun setBaseBackspaceSpamSpeed(context: Context, intValue: Int) =
        checkAndSetUserDefinedValue(context, intValue, GriddleSetting.MINIMUM_BACKSPACE_SPAMMING_SPEED)

    fun getUserPrimaryLanguage(context: Context): LanguageTag {
        val languageOrdinal = getPreferences(context).getInt(PRIMARY_LANGUAGE, -1)
        return if (languageOrdinal !in 0..<LanguageTag.entries.size) {
            setPrimaryLanguage(context, LanguageTag.ENGLISH)
            LanguageTag.ENGLISH
        } else {
            LanguageTag.entries[languageOrdinal]
        }
    }

    fun setPrimaryLanguage(context: Context, languageTag: LanguageTag) {
        getPreferences(context).edit().putInt(PRIMARY_LANGUAGE, languageTag.ordinal).apply()
    }

    fun getUserPreferredLanguages(context: Context): Set<LanguageTag> {
        val languageOrdinals = getIntegerList(context, LANGUAGE_PREFERENCES)
        val toSet = languageOrdinals.mapNotNull { LanguageTag.entries.getOrNull(it) }.toSet()
        if(toSet.isEmpty()) {
            setPrimaryLanguage(context, LanguageTag.ENGLISH)
            putAllLanguages(context, setOf(getUserPrimaryLanguage(context)))
            return setOf(LanguageTag.ENGLISH)
        }
        return toSet
    }

    private fun putAllLanguages(context: Context, of: Set<LanguageTag>) {
        val editor = getPreferences(context).edit()
        editor.putStringSet(LANGUAGE_PREFERENCES, of.map { it.ordinal.toString() }.toSet())
        editor.apply()
    }

    // Store the list of integers
    private fun storeIntegerList(context: Context, key: String, intList: List<Int>) {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val stringSet = intList.map { it.toString() }.toSet()
        editor.putStringSet(key, stringSet)
        editor.apply()
    }

    // Retrieve the list of integers
    private fun getIntegerList(context: Context, key: String): List<Int> {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val stringSet = prefs.getStringSet(key, emptySet())
        return stringSet?.map { it.toInt() } ?: emptyList()
    }

    fun setAllLanguages(context: Context, languageTags: Set<LanguageTag>) {
        putAllLanguages(context, languageTags)
    }

    fun saveBoardPositionAndSize(
        context: Context,
        currentPositionAndSize: KeyboardOffsetAndSize,
        qualifiedName: String?,
    ) {
        val editor = getPreferences(context).edit()
        val (offsetX, width, height) = currentPositionAndSize
        if(offsetX < 0
            || width < DEFAULT_SIZE.width / 4
            || height < DEFAULT_SIZE.height / 4
        ) {
            editor.putFloat("$qualifiedName::$BOARD_POSITION_X", -1f)
            editor.putFloat("$qualifiedName::$BOARD_POSITION_WIDTH", -1f)
            editor.putFloat("$qualifiedName::$BOARD_POSITION_HEIGHT", -1f)
        }
        editor.putFloat("$qualifiedName::$BOARD_POSITION_X", offsetX)
        editor.putFloat("$qualifiedName::$BOARD_POSITION_WIDTH", width)
        editor.putFloat("$qualifiedName::$BOARD_POSITION_HEIGHT", height)
        editor.apply()
    }

    fun getBoardPositionAndSize(context: Context, qualifiedName: String
    ): KeyboardOffsetAndSize {
        val prefs = getPreferences(context)
        val offsetX = prefs.getFloat("$BOARD_POSITION_X::$qualifiedName", -1f)
        val width = prefs.getFloat("$BOARD_POSITION_WIDTH::$qualifiedName", -1f)
        val height = prefs.getFloat("$BOARD_POSITION_HEIGHT::$qualifiedName", -1f)
        if(offsetX < 0
            || width < DEFAULT_SIZE.width / 4
            || height < DEFAULT_SIZE.height / 4
        ) {
            val (screenWidth, screenHeight) = context.resources.displayMetrics
                .let{ it.widthPixels to it.heightPixels }
            val newWidth = (DEFAULT_SIZE.width.toFloat() * 4)
                .coerceAtMost(screenWidth.toFloat())
            val newHeight = (DEFAULT_SIZE.height.toFloat() * 4)
                .coerceAtMost((screenHeight * .45f))
            val offsetX = (screenWidth - newWidth) / 2
            val currentPositionAndSize = KeyboardOffsetAndSize(offsetX, newWidth, newHeight)
            saveBoardPositionAndSize(context, currentPositionAndSize, qualifiedName)
            return currentPositionAndSize
        }
        return KeyboardOffsetAndSize(offsetX, width, height)
    }

    fun getAutoPunctuationPreference(context: Context): Boolean {
        if(!getPreferences(context).contains(AUTO_PUNCTUATION)) {
            getPreferences(context).edit().putBoolean(AUTO_PUNCTUATION, false).apply()
        }
        return getPreferences(context).getBoolean(AUTO_PUNCTUATION, false)
    }

    fun setAutoPunctuationPreference(context: Context, autoPeriod: Boolean) {
        getPreferences(context).edit().putBoolean(AUTO_PUNCTUATION, autoPeriod).apply()
    }

    fun getAutoCorrectionPreference(context: Context): Boolean {
        if(!getPreferences(context).contains(AUTO_CORRECTION)) {
            getPreferences(context).edit().putBoolean(AUTO_CORRECTION, false).apply()
        }
        return getPreferences(context).getBoolean(AUTO_CORRECTION, false)
    }

    fun setAutoCorrectionPreference(context: Context, autoCorrection: Boolean) {
        getPreferences(context).edit().putBoolean(AUTO_CORRECTION, autoCorrection).apply()
    }

    fun getAutoCapitalizationPreference(context: Context): Boolean {
        if(!getPreferences(context).contains(AUTO_CAPS)) {
            getPreferences(context).edit().putBoolean(AUTO_CAPS, false).apply()
        }
        return getPreferences(context).getBoolean(AUTO_CAPS, false)
    }

    fun setAutoCapitalizationPreference(context: Context, autoCapitalization: Boolean) {
        getPreferences(context).edit().putBoolean(AUTO_CAPS, autoCapitalization).apply()
    }

    fun getAppSymbolSize(context: Context, symbol: AppSymbol): Float {
        return getPreferences(context).getFloat("${symbol.name}.size", -1f)
    }

    fun setAppSymbolSize(context: Context, symbol: AppSymbol, fontSizeValue: Float) {
        getPreferences(context).edit().putFloat("${symbol.name}.size", fontSizeValue).apply()
    }

    fun setTurboModeChoice(context: Context, newValue: Int) {
        checkAndSetUserDefinedValue(context, newValue, GriddleSetting.IS_TURBO_ENABLED)
    }
}