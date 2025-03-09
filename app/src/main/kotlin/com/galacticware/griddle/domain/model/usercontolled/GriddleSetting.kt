package com.galacticware.griddle.domain.model.usercontolled

import android.content.Context
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import kotlin.math.roundToInt

/**
 * Enumeration of all settings that can be modified by incrementing or decrementing a value within a given range, with a
 * defined step size.
 */
enum class GriddleSetting(
    val defaultValue: Int,
    val minValue: Int,
    val stepSize: Int,
    val steps: Int,
    val isToggleable: Boolean = false,
    val prettyName: String,
) {
    MINIMUM_HOLD_TIME(
        defaultValue = 50,
        minValue = 250,
        stepSize = 10,
        steps = 175,
        prettyName = "minimum hold time",
    ),

    IS_VIBRATION_ENABLED(
        defaultValue = VibrationChoice.ON.ordinal,
        minValue = 0,
        stepSize = 1,
        steps = 1,
        isToggleable = true,
        prettyName = "vibration toggle",
    ),

    IS_TURBO_MODE_ENABLED(
        defaultValue = VibrationChoice.entries.let{ it[it.size - 1].ordinal },
        minValue = TurboModeChoice.entries[0].ordinal,
        stepSize = 1,
        steps = 1,
        isToggleable = true,
        prettyName = "turbo mode toggle",
    ),

    VIBRATION_AMPLITUDE(
        defaultValue = 50,
        minValue = 1,
        stepSize = 10,
        steps = 25,
        prettyName = "vibration amplitude",
    ),

    MINIMUM_DRAG_LENGTH(
        defaultValue = 100,
        minValue = 5,
        stepSize = 5,
        steps = 31,
        prettyName = "minimum drag length",
    ),

    IS_GESTURE_TRACING_ENABLED(
        defaultValue = GestureTracingChoice.ON.ordinal,
        minValue = 0,
        stepSize = 1,
        steps = 1,
        isToggleable = true,
        prettyName = "gesture tracing visibility",
    ),

    /**
     * It looks like Minimum drag length is working well for limiting drags and circles that are small, but if we really
     * do need two different settings, then we can use the following enum.
     * This value can be used by the logic in the provided gesture detector implementation.
     */
    MINIMUM_CIRCLE_RADIUS(
        defaultValue = 160,
        minValue = 45,
        stepSize = 2,
        steps = 80,
        prettyName = "minimum circle radius",
    ),

    /**
     * The minimum amount of time that the user must hold their finger on the screen before a hold
     * is registered. It has a total number of 499 steps, with a step size of 1, and a minimum
     * value of 1. This means that the maximum value is:
     * 1 + 1 * 499 = 1 + 499 = 500 == 500 milliseconds.
     */
    MINIMUM_BACKSPACE_SPAMMING_SPEED(
        defaultValue = 10,
        minValue = 1,
        stepSize = 1,
        steps = 499,
        prettyName = "minimum backspace spamming speed",
    ),
    ;

    fun currentSettingValueOf(context: Context): Int = when (this) {
        IS_GESTURE_TRACING_ENABLED -> PreferencesHelper.getGestureTracingChoice(context).ordinal
        MINIMUM_HOLD_TIME -> PreferencesHelper.getMinimumHoldTime(context)
        IS_VIBRATION_ENABLED -> PreferencesHelper.getUserVibrationChoice(context).ordinal
        IS_TURBO_MODE_ENABLED -> PreferencesHelper.getTurboModeChoice(context).ordinal
        VIBRATION_AMPLITUDE -> PreferencesHelper.getUserVibrationAmplitude(context)
        MINIMUM_DRAG_LENGTH -> PreferencesHelper.getMinimumDragLength(context)
        MINIMUM_CIRCLE_RADIUS -> PreferencesHelper.getMinimumCircleRadius(context)
        MINIMUM_BACKSPACE_SPAMMING_SPEED -> PreferencesHelper.getBaseBackspaceSpamSpeed(context).roundToInt()
        else -> this.defaultValue
    }
    fun setValue(context: Context, newValue: Int) {
        when(this) {
            IS_VIBRATION_ENABLED -> PreferencesHelper.setUserVibrationChoice(
                context,
                VibrationChoice.entries[newValue
                    .coerceAtMost(VibrationChoice.entries.size - 1)
                    .coerceAtLeast(0)
                ].ordinal,
            )
            IS_GESTURE_TRACING_ENABLED -> PreferencesHelper.setGestureTracingChoice(
                context,
                GestureTracingChoice.entries[newValue
                    .coerceAtMost(GestureTracingChoice.entries.size - 1)
                    .coerceAtLeast(0)
                ].ordinal,
            )
            VIBRATION_AMPLITUDE -> PreferencesHelper.setUserVibrationAmplitude(context, newValue)
            MINIMUM_HOLD_TIME -> PreferencesHelper.setMinimumHoldTime(context, newValue)
            MINIMUM_DRAG_LENGTH -> PreferencesHelper.setMinimumDragLength(context, newValue)
            MINIMUM_CIRCLE_RADIUS -> PreferencesHelper.setMinimumCircleRadius(context, newValue)
            MINIMUM_BACKSPACE_SPAMMING_SPEED -> PreferencesHelper.setBaseBackspaceSpamSpeed(context, newValue)
            IS_TURBO_MODE_ENABLED -> PreferencesHelper.setTurboModeChoice(context, newValue)
            else -> throw NotImplementedError("Changing this setting ($this) is not yet supported.")
        }
        UserDefinedValues.currentData(context)
    }

    val maxValue: Int = minValue + stepSize * steps
}