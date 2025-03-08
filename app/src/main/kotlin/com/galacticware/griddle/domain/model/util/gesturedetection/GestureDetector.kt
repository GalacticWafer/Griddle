package com.galacticware.griddle.domain.model.util.gesturedetection

import com.galacticware.griddle.domain.model.shared.gesturedetecton.IGestureDetector
import com.galacticware.griddle.domain.model.shared.gesturedetecton.MessagEaseStyleGestureDetector


/**
 * This is where you specify the gesture detector that you want to use.
 * Either implement an [IGestureDetector] here, or point to a different one.
 */
val GestureDetector : IGestureDetector by lazy {
    MessagEaseStyleGestureDetector
}
