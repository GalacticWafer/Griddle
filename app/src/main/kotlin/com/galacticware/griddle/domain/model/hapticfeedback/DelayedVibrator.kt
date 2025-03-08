package com.galacticware.griddle.domain.model.hapticfeedback

import android.content.Context
import com.galacticware.griddle.domain.model.usercontolled.GriddleSetting
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues
import com.galacticware.griddle.domain.model.usercontolled.VibrationChoice
import com.galacticware.griddle.domain.model.util.vibrate

/**
 * This is our solution to performing vibration at some time in the future after the user lifts the pointer from the
 * screen.
 */
class DelayedVibrator(
    private val context: Context,
) {
    var userVibration = UserDefinedValues.current.userVibration
    fun vibrate() {
        val userVibration = UserDefinedValues.current.userVibration
        if(userVibration.toggledChoice == VibrationChoice.OFF) return
        if(userVibration.amplitude <= GriddleSetting.VIBRATION_AMPLITUDE.minValue) return
        vibrate(context)
    }
    fun vibrate(userVibration: UserVibration) {
        this.userVibration = userVibration
        vibrate()
    }
}