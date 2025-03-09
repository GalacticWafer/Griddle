package com.galacticware.griddle.domain.model.gesture

import com.galacticware.griddle.domain.model.shared.gesturedetection.DefaultGestureDetector
import com.galacticware.griddle.domain.model.gesture.detection.CustomGestureDetectorProvider

val gestureDetector get() =
    CustomGestureDetectorProvider().provideGestureDetector() ?: DefaultGestureDetector