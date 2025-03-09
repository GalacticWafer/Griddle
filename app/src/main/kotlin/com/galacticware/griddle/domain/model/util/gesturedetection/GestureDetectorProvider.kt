package com.galacticware.griddle.domain.model.util.gesturedetection

import com.galacticware.griddle.domain.model.shared.gesturedetection.IGestureDetector

interface GestureDetectorProvider {
    fun provideGestureDetector(): IGestureDetector?
}