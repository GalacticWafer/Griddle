
package com.galacticware.griddle.domain.model.operation.base

import android.content.Context
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture


interface OperationDescriptionProvider {
    val requiresUserInput: Boolean
    val userHelpDescription: String
    val menuItemDescription: String
    val name: String
    val tag: OperationTag

    /**
     * Show the screen for reassigning this gesture.
     * @param context The context to use for showing the screen.
     * @param gesture The gesture to reassign.
     */
    @Composable
    fun ShowReassignmentScreen(context: Context, gesture: Gesture)
    fun produceNewGesture(gesturePrototype: Gesture): Gesture
}