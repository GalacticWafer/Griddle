package com.galacticware.griddle.domain.model.util.gesturedetection

import com.galacticware.griddle.domain.model.shared.gesturedetection.DefaultGestureDetector
import com.galacticware.griddle.domain.model.shared.gesturedetection.IGestureDetector

/**
 * This is where you specify the gesture detector that you want to use.
 * Either implement an [IGestureDetector] here, or point to a different one.
 * If this field is null, then the [DefaultGestureDetector] will be used.
 */
class CustomGestureDetectorProvider : GestureDetectorProvider {
    override fun provideGestureDetector(): IGestureDetector? = null
}
