package com.galacticware.griddle.domain.model.keyboard

import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GesturePerformanceInfo
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.input.AppInputFocus
import com.galacticware.griddle.domain.model.input.GriddleInputConnection
import com.galacticware.griddle.domain.model.input.IMEService
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.operation.base.SavedExecution
import com.galacticware.griddle.domain.model.operation.implementation.noargs.backspace.BaseBackspaceOperation
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.model.shared.Point
import com.galacticware.griddle.domain.model.shared.gesturedetection.IGestureDetector
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues
import com.galacticware.griddle.domain.view.composable.nestedappscreen.BuildYourOwnKeyboardScreen
import javax.inject.Inject

data class KeyboardContextBuilder(
    var applicationContext: Context,
    var previousOperation: SavedExecution,
    var keyboard: Keyboard,
) {
    @Inject lateinit var gestureDetector : IGestureDetector
    private var gestureButton: GestureButton? = null
    var touchPoints: MutableList<Point> = mutableListOf()
    var gesture: Gesture? = null
    var view: View? = null
    var gestureButtonPosition: GridPosition? = null
    var buttonContainingLastPoint: GestureButton? = null
    private var doNotRun = true
    lateinit var inputConnection: GriddleInputConnection
    init {
        try {
            inputConnection = GriddleInputConnection(
                (applicationContext as IMEService).currentInputConnection,
                applicationContext
            )
            doNotRun = false
        } catch (e: Exception) {
            doNotRun = true
        }
    }

    fun build(): KeyboardContext {
        return KeyboardContext(
            keyboard = keyboard,
            context = applicationContext,
            gesture = gesture!!,
            touchPoints = touchPoints,
            view = view,
            previousOperation = previousOperation,
            gestureButtonPosition = gestureButton!!.gridPosition,
        )
    }

    fun perform(
        buttonContainingFirstPoint: GestureButton,
        event: MotionEvent,
        isTurboModeEnabled: Boolean,
    ): GesturePerformanceInfo = run {
        val userDefinedValues = UserDefinedValues.current
        val (genericGestureType, swipeDirection, rotationDirection) = gestureDetector.determineGesture(
            minimumHoldTime = userDefinedValues.minimumHoldTime,
            minimumDragLength = userDefinedValues.minimumDragLength,
            duration = event.eventTime - event.downTime,
            touchPoints
        )
        this.gestureButton = buttonContainingFirstPoint
        gesture = buttonContainingFirstPoint.let { btn ->
            btn.keyBindingForType(
                genericGestureType = genericGestureType,
                rotationDirection = rotationDirection,
                direction = swipeDirection,
            )?.let {
                btn.nullOnTurboMode(isTurboModeEnabled, it)
            }
        }

        if(doNotRun) {
            return GesturePerformanceInfo(
                gesture,
                previousOperation,
                swipeDirection,
                genericGestureType,
                buttonContainingFirstPoint,
                buttonContainingLastPoint,
                rotationDirection,
            )
        }
        gesture?.let { aGesture -> buttonContainingFirstPoint.let { aGestureButton ->
            /* Perform the gesture action */
                if(aGesture.currentAssignment.operation.isBackspace) {
                    // turn off shift
                    Keyboard.cancelModifier(ModifierKeyKind.SHIFT)
                    Keyboard.didLastActionAutoCapitalize = false
                }
                if(NestedAppScreen.stack.peek() == BuildYourOwnKeyboardScreen
                    /*&& !BuildYourOwnKeyboardScreen.isWaitingForTextInput*/) {
                    GriddleInputConnection.inputFocus = AppInputFocus.SIMPLE_INPUT_ASSIGNMENT
/*
                    return GesturePerformanceInfo(
                        gesture,
                        previousOperation,
                        direction,
                        genericGestureType,
                        buttonContainingFirstPoint,
                        buttonContainingLastPoint,
                        rotationDirectionProvider()
                    )
*/
                }
                previousOperation = aGesture.perform(
                    keyboard,
                    applicationContext,
                    touchPoints,
                    view,
                    previousOperation,
                    aGestureButton.gridPosition,
                )
        }}
        Keyboard.wasLastActionBackspace = previousOperation.op is BaseBackspaceOperation
        return GesturePerformanceInfo(
            gesture,
            previousOperation,
            swipeDirection,
            genericGestureType,
            buttonContainingFirstPoint,
            buttonContainingLastPoint,
            rotationDirection
        )
    }

    fun clearPointsThenAdd(point: Point) {
        gesture = null
        touchPoints.clear()
        touchPoints.add(point)
        view = null
        gestureButtonPosition = null
    }

    fun updateInputConnection(context: Context) {
        inputConnection = GriddleInputConnection(
            (context as IMEService).currentInputConnection,
            context
        )
    }
}
