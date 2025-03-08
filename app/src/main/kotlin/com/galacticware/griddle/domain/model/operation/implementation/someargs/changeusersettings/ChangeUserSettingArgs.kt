package com.galacticware.griddle.domain.model.operation.implementation.someargs.changeusersettings


import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.usercontolled.GriddleSetting
import com.galacticware.griddle.domain.model.usercontolled.GriddleSetting.*
import com.galacticware.griddle.domain.model.usercontolled.IncrementalAdjustmentType
import com.galacticware.griddle.domain.model.usercontolled.IncrementalAdjustmentType.*
import kotlinx.serialization.Serializable

@Serializable
data class ChangeUserSettingArgs(
    val griddleSetting: GriddleSetting,
    val incrementalAdjustmentType: IncrementalAdjustmentType,
): OperationArgs() {
    override fun description(): String = "${incrementalAdjustmentType.capsed} the ${griddleSetting.prettyName}"
    override fun opInstance(): ParameterizedOperation<*> = ChangeUserSetting
    companion object {
        val IncreaseVibrationAmplitude by lazy { ChangeUserSettingArgs(VIBRATION_AMPLITUDE, INCREASE) }
        val DecreaseVibrationAmplitude by lazy { ChangeUserSettingArgs(VIBRATION_AMPLITUDE, DECREASE) }
        val ToggleVibration by lazy { ChangeUserSettingArgs(IS_VIBRATION_ENABLED, TOGGLE) }
        val ToggleTurboMode by lazy { ChangeUserSettingArgs(IS_TURBO_ENABLED, DECREASE) }
        val ToggleGestureTracing by lazy { ChangeUserSettingArgs(IS_GESTURE_TRACING_ENABLED, DECREASE) }
        val DecreaseMinimumDragLength by lazy { ChangeUserSettingArgs(MINIMUM_DRAG_LENGTH, DECREASE) }
        val IncreaseMinimumDragLength by lazy { ChangeUserSettingArgs(MINIMUM_DRAG_LENGTH, INCREASE) }

        val instances by lazy {
            setOf(
                IncreaseVibrationAmplitude, DecreaseVibrationAmplitude, ToggleVibration,
                ToggleTurboMode,
                ToggleGestureTracing
            )
        }
    }
}