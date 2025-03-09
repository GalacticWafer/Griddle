package com.galacticware.griddle.domain.model.gesture.detection

import com.galacticware.griddle.domain.model.shared.gesturedetection.IGestureDetector

/**
 * Simple provider class for the gesture detector.
 */
interface GestureDetectorProvider {
    fun provideGestureDetector(): IGestureDetector?
}