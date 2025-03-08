package com.galacticware.griddle.domain.model.hapticfeedback

import com.galacticware.griddle.domain.model.usercontolled.VibrationChoice

data class UserVibration(
    val amplitude: Int,
    val toggledChoice: VibrationChoice,
)