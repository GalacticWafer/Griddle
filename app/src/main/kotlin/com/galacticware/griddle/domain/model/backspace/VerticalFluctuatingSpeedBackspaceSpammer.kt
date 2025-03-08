package com.galacticware.griddle.domain.model.backspace

import android.content.Context
import android.util.DisplayMetrics
import android.view.MotionEvent
import androidx.compose.ui.unit.Density
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.geometry.StartAndSpan
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyspammer.BackspaceSpammer
import com.galacticware.griddle.domain.model.operation.base.SavedExecution
import com.galacticware.griddle.domain.model.operation.implementation.noargs.cursorcontrol.DeleteWordLeftGesture
import com.galacticware.griddle.domain.model.operation.implementation.noargs.noop.NoOp
import java.util.Timer
import java.util.TimerTask

/**
 * Too verbose? Or extremely accurate? You decide.
 * This Backspace dynamically speeds up when you drag your finger toward the top
 * of the screen, and slows down when you drag your finger toward the bottom of the
 * screen. You have to press and hold for the minimum hold duration on the
 * backspace key to activate this functionality.
 */
object VerticalFluctuatingSpeedBackspaceSpammer: BackspaceSpammer() {
    private const val MIN_INTERVAL = 20L
    private const val STEP_THRESHOLD = 5f
    private const val THRESHOLD_FOR_SPAMMING_WORDS = .7f

    @Volatile private var speed: Float = 0f
    var mostRecentMotionEventAction = MotionEvent.ACTION_UP
    private var buttonYOffset = 0.0
    private var width: Int = 0
    private var keyboardHeight: Float = 0f

    fun start(
        keyboard: Keyboard,
        applicationContext: Context,
        startAndSpan: StartAndSpan,
        rowHeight: Int,
        localDensity: Density
    ) {
        mostRecentMotionEventAction = MotionEvent.ACTION_UP
        val timer = Timer()
        val metrics: DisplayMetrics = applicationContext.resources.displayMetrics
        width = metrics.widthPixels
        keyboardHeight = with(localDensity) { keyboard.height.toPx() }
        val boxStart = startAndSpan.start * rowHeight
        val boxEnd = boxStart + startAndSpan.span * rowHeight
        buttonYOffset = keyboardHeight - boxEnd.toDouble()
        var count = 0f

        val keyboardContext = KeyboardContext(
            keyboard,
            applicationContext,
            Gesture.DUMMY_CLICK,
            listOf(),
            null,
            null,
            GridPosition.originUnit,
        )

        mostRecentMotionEventAction = MotionEvent.ACTION_DOWN
        speed = 0.1f
        timer.schedule(
            object : TimerTask() {
                override fun run() {

                    if (mostRecentMotionEventAction == MotionEvent.ACTION_UP) {
                        cancel()
                        timer.purge()
                        return
                    }
                    count += speed
                    if (count >= STEP_THRESHOLD) {
                        if (speed > THRESHOLD_FOR_SPAMMING_WORDS) {
                            DeleteWordLeftGesture.perform(
                                keyboardContext,
                                applicationContext,
                                savedExecution = SavedExecution(
                                NoOp
                            ) {})
                        } else {
                            keyboardContext.inputConnection.pressKey(backspaceDown)
                            keyboardContext.inputConnection.pressKey(backspaceUp)
                        }
                        count = 0f
                    }
                }
            },
            0,
            MIN_INTERVAL
        )
    }

    fun stop() {
        mostRecentMotionEventAction = MotionEvent.ACTION_UP
    }

    fun report(y: Int, context: Context) {
        if(mostRecentMotionEventAction == MotionEvent.ACTION_UP) return
        val distanceToBottomOfScreen = context.resources.displayMetrics.heightPixels - y
        speed = distanceToBottomOfScreen / keyboardHeight
    }
}
