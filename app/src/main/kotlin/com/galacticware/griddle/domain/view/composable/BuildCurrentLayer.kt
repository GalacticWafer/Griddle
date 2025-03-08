package com.galacticware.griddle.domain.view.composable

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galacticware.griddle.domain.model.backspace.VerticalFluctuatingSpeedBackspaceSpammer
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.designer.KeyboardPart
import com.galacticware.griddle.domain.model.shared.Point
import com.galacticware.griddle.domain.model.shared.GenericGestureType
import com.galacticware.griddle.domain.model.hapticfeedback.DelayedVibrator
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.Keyboard.Companion.currentLayerKind
import com.galacticware.griddle.domain.model.keyboard.KeyboardContextBuilder
import com.galacticware.griddle.domain.model.keyboard.KeyboardKind
import com.galacticware.griddle.domain.model.operation.base.SavedExecution
import com.galacticware.griddle.domain.model.operation.implementation.noargs.backspace.BaseBackspaceOperation
import com.galacticware.griddle.domain.model.operation.implementation.noargs.noop.NoOp
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.model.shared.gesturedetecton.IGestureDetector.Companion.DEFAULT_MAXIMUM_DISTANCE_TO_REMOVE_JITTER
import com.galacticware.griddle.domain.model.usercontolled.GestureTracingChoice
import com.galacticware.griddle.domain.model.usercontolled.TurboModeChoice
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.model.util.gesturedetection.GestureDetector
import com.galacticware.griddle.domain.view.composable.nestedappscreen.BuildYourOwnKeyboardScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.WordPredictionScreen
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel
import kotlinx.coroutines.delay
import kotlin.math.hypot

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun BuildCurrentLayer(
    keyboard: Keyboard,
    context: Context = LocalContext.current,
    isDesignerMode: Boolean = false,
    scale: Float = keyboard.userDefinedScale,
) {
    Keyboard.currentLayer?: return
    currentLayerKind = Keyboard.currentLayer!!.layerKind
    val qualifiedName = Keyboard.currentLayer!!.name
    val board by remember {
        val (_, savedWidth, savedHeight)
        = PreferencesHelper.getBoardPositionAndSize(context, qualifiedName)
        keyboard.width = savedWidth.dp
        keyboard.height = savedHeight.dp
        mutableStateOf(keyboard)
    }
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    var boardHeight by remember { mutableStateOf(board.height) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            suggestions = WordPredictionScreen.suggestions
        }
    }

    // Keep track of the current word predictions
    // Run an inference on the words before the cursor
    var newPredictions by remember { mutableStateOf(Keyboard.predictions) }
    var buttons by remember { mutableStateOf(board.buttons) }
    LaunchedEffect(Unit) {
        while (true) {
            boardHeight = board.height
            val loadButtons = board.buildButtons()
            if (newPredictions != Keyboard.predictions) {
                newPredictions = Keyboard.predictions
            }
            if(buttons != loadButtons) {
                buttons = loadButtons
            }
            delay(100) // Adjust the delay as needed
        }
    }

    val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
    var previousOperation: SavedExecution by remember {
        mutableStateOf(SavedExecution(NoOp) {})
    }
    var userDefinedValues by remember { mutableStateOf(UserDefinedValues.current) }

    // We need to remember layout coordinates for the backspace spammer
    var layoutCoordinates by remember { mutableStateOf(null as LayoutCoordinates?) }

    val currentView = LocalView.current
    val isTurboModeEnabled = userDefinedValues.turboModeChoice == TurboModeChoice.ON
    Column(
        Modifier
            .scale(scale)
            //.border(2.dp, Color.Red)
    ) {
        if(Keyboard.isWordPredictionEnabled) {
            Row(
                modifier = Modifier.wrapContentSize()
            ) {
                WordPredictionScreen.ShowPredictions(newPredictions)
            }
        }
        Row(
            Modifier
            //.border(1.dp, Color.Green)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(0.dp)
                //            .border(10.dp, AppColor.ORANGE.color)
            ) {
                val onGloballyPositionedModifier = Modifier
                    .onGloballyPositioned { layoutCoordinates = it }
                    .let {
                        it.requiredWidth(
                            if (board.keyboardKind == KeyboardKind.SINGLE_BUTTON_DESIGNER_MODE) {
                                board.primaryLayer.gestureButtonBuilders
                                    .first()
                                    .let { b -> b.size.width * b.colSpan }.dp
                            } else
                                board.width
                        )
                    }
                var initialGestureButton: GestureButton? by remember { mutableStateOf(null) }
                Box(
                    modifier = onGloballyPositionedModifier
                        .requiredHeight(
                            if (board.keyboardKind == KeyboardKind.SINGLE_BUTTON_DESIGNER_MODE) {
                                board.primaryLayer.gestureButtonBuilders
                                    .first()
                                    .let { b -> b.size.height * b.rowSpan }.dp
                            } else boardHeight
                        )
                        .align(Alignment.BottomStart)
                        .border(width = 1.dp, color = Color.Transparent)
                        .background(Color.Transparent)
                ) {
                    val localDensity = LocalDensity.current
                    val updatedButtons = buttons.map {
                        UpdateGesturesContainingTextProviders(it)
                    }
                    updatedButtons.forEach { (gestureButton, themeAndGesturePairs) ->
                        val withHandedness = Keyboard.withHandedness(
                            board.colSpan,
                            userDefinedValues.userHandedness,
                            board.rotatedColumnCount,
                            gestureButton.colStart
                        )
                        val buttonOffsetY = gestureButton.rowStart * board.rowHeight
                        val buttonOffsetX = withHandedness * board.colWidth

                        val modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = buttonOffsetX.dp.roundToPx(),
                                    y = buttonOffsetY.dp.roundToPx(),
                                )
                            }
                            .height((gestureButton.rowSpan.toFloat() * board.rowHeight).dp)
                            .width((gestureButton.colSpan.toFloat() * board.colWidth).dp)

                        Box(
                            modifier = modifier
                                .testTag("android:id/`button_${gestureButton.rowStart}_${gestureButton.colStart}`")
                                .border(
                                    width = .5.dp,
                                    gestureButton.modifierTheme.primaryBorderColor
                                )
                                .background(gestureButton.modifierTheme.let { buttonDefaultTheme ->
                                    if (gestureButton.isPeripheral) buttonDefaultTheme.secondaryBackgroundColor else buttonDefaultTheme.primaryBackgroundColor
                                }),
                            contentAlignment = Alignment.Center,
                        ) {
                            PutThemeAndGesturePairs(themeAndGesturePairs, gestureButton, isTurboModeEnabled, localDensity, board)
                        }

                        /**
                         * Invisible gesture detection overlay is a set of buttons with the same sizes and positions as the
                         * ones we just made. This way, we element to detect all gestures on the
                         * board.
                         */
                        val time = System.currentTimeMillis()
                        val keyboardContextBuilder = remember {
                            KeyboardContextBuilder(
                                context,
                                previousOperation,
                                keyboard
                            ).apply {
                                this.keyboard = board
                                applicationContext = context
                                view = currentView
                                gestureButtonPosition = gestureButton.gridPosition
                            }
                        }
                        val velocityTracker = remember { VelocityTracker.obtain() }
                        var isPointerStillBeingHeld by remember { mutableStateOf(System.nanoTime() as Long?) }
                        val vibrator = remember { DelayedVibrator(context) }

                        var displayPoints by remember { mutableStateOf(listOf<Point>()) }
                        if (UserDefinedValues.current.isGestureTracingEnabled == GestureTracingChoice.ON
                            && !keyboardContextBuilder.inputConnection.isPasswordField(EditorInfo())) {
                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(10)
                                    displayPoints = keyboardContextBuilder.touchPoints.filter { it.opacity > 0f }
                                }
                            }
                        }

                        val backspaceSpammer = remember { VerticalFluctuatingSpeedBackspaceSpammer }

                        Box(modifier = modifier
                            .background(Color.Transparent)
                            .border(0.dp, Color.Transparent)
                            .zIndex(2f)
                            .pointerInteropFilter { event: MotionEvent ->
                                // This is a stylus, ignore it
                                if (event.pressure == 0f) return@pointerInteropFilter true
                                // Do not interfere with board movement and resizing.
                                if(Keyboard.isResizingAndMoving) return@pointerInteropFilter true

                                val p2 = Point(event.x.toDouble(), event.y.toDouble())
                                when (event.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        initialGestureButton = gestureButton
                                        isPointerStillBeingHeld = System.nanoTime()
                                        keyboardContextBuilder.updateInputConnection(context)
                                        keyboardContextBuilder.touchPoints.clear()
                                        velocityTracker.clear()
                                        keyboardContextBuilder.clearPointsThenAdd(p2)
                                        GestureDetector.reset()

                                        if(NestedAppScreen.stack.peek() == BuildYourOwnKeyboardScreen) {
                                            return@pointerInteropFilter true
                                        }
                                        /***
                                         *  Check if there is any HOLD gesture bound to a Backspace operation,
                                         *  and if so, start the backspace spammer.
                                         */
                                        gestureButton.keyBindingForType(GenericGestureType.HOLD)?.let {
                                            if (it.editorOperation is BaseBackspaceOperation) {
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    if(isPointerStillBeingHeld != null) {
                                                        backspaceSpammer.start(
                                                            board,
                                                            context,
                                                            gestureButton.gridPosition.rowParams.startAndSpan,
                                                            board.rowHeight,
                                                            localDensity,
                                                        )
                                                    } else backspaceSpammer.stop()
                                                }, 500)
                                            }
                                        }
                                    }

                                    MotionEvent.ACTION_MOVE -> {
                                        if (initialGestureButton?.gridArea?.position != gestureButton.gridArea.position) {
                                            return@pointerInteropFilter true
                                        }
                                        keyboardContextBuilder.touchPoints.let { touchPoints ->
                                            if (touchPoints.isEmpty() || isPointerStillBeingHeld != null && hypot(
                                                    (p2.y - touchPoints.last().y),
                                                    (p2.x - touchPoints.last().x)
                                                ) > DEFAULT_MAXIMUM_DISTANCE_TO_REMOVE_JITTER
                                            ) {
                                                touchPoints.add(p2)
                                            }
                                        }
                                        if (NestedAppScreen.stack.peek() == BuildYourOwnKeyboardScreen) {
                                            return@pointerInteropFilter true
                                        }
                                        backspaceSpammer.report(event.rawY.toInt(), context)
                                    }

                                    /* Complete and detect gesture, execute action */
                                    MotionEvent.ACTION_UP -> {
                                        if (initialGestureButton?.gridArea?.position != gestureButton.gridArea.position) {
                                            return@pointerInteropFilter true
                                        }
                                        isPointerStillBeingHeld = null
                                        userDefinedValues.userVibration.let { currentVibration ->
                                            vibrator.vibrate(currentVibration)
                                        }
                                        backspaceSpammer.stop()

                                        /**
                                         * We don 't know how many points will be in the gesture until the gesture is finished. Therefore, in order to account
                                         * for the magnitude and direction of the entire gesture, we add a trailing point to the list of touch points, created
                                         * from the the velocity, and append it only if the event is an ACTION_UP event to ensure it represents the final
                                         * portion of the gesture.
                                         * */
                                        velocityTracker.computeCurrentVelocity(1)
                                        val velocityX = event.x.toDouble() + velocityTracker.xVelocity.toDouble()
                                        val velocityY = event.y.toDouble() + velocityTracker.yVelocity.toDouble()
                                        val velocityPoint = Point(velocityX, velocityY)
                                        keyboardContextBuilder.touchPoints.add(velocityPoint)
                                        keyboardContextBuilder.touchPoints.forEach { it.reportUp(time) }

                                        // Figure out which button the last point landed on, if any
                                        keyboardContextBuilder.apply {
                                            this.previousOperation = previousOperation
                                            val lastPoint = touchPoints.last()
                                            val (lastPointX, lastY) = lastPoint.roundToInt()
                                            this.buttonContainingLastPoint = lastPoint.let {
                                                buttons.entries.firstOrNull { (currentButtonLocation, buttonParams) ->
                                                    val (buttonLeft, buttonTop) = currentButtonLocation
                                                    val currentButtonWidth = buttonParams.first.width
                                                    val currentButtonHeight = buttonParams.first.height
                                                    val buttonRight = buttonLeft + currentButtonWidth
                                                    val buttonBottom = buttonTop + currentButtonHeight
                                                    lastPointX in buttonLeft..buttonRight && lastY in buttonTop..buttonBottom
                                                }?.value?.first
                                            }
                                        }

                                        val gesturePerformanceInfo = keyboardContextBuilder.perform(
                                            initialGestureButton!!,
                                            event,
                                            isTurboModeEnabled
                                        )
                                        if (NestedAppScreen.stack.peek() == BuildYourOwnKeyboardScreen) {
                                            byokViewModel.setCurrentlySelectedGestureInfo(gesturePerformanceInfo)
                                            byokViewModel.setCurrentlyEditedKeyboardPart(KeyboardPart.GESTURE)
                                            return@pointerInteropFilter true
                                        }
                                        initialGestureButton = null
                                        previousOperation = gesturePerformanceInfo.previousOperation
                                    }
                                }
                                userDefinedValues = UserDefinedValues.currentData(context)
                                true
                            }
                        )

                        val offsetY = if(isDesignerMode) {
                            (layoutCoordinates?.positionInRoot()?.y ?: 0f)
                        } else 0f

                        val gestureTrailModifier = Modifier.matchParentSize()
                            .zIndex(Float.MAX_VALUE)
                            .background(Color.Transparent)
                        if (initialGestureButton == gestureButton) {
                            DrawGestureTrail(displayPoints, localDensity, buttonOffsetX, buttonOffsetY, offsetY, gestureTrailModifier)
                        }
                    }
                }
            }
        }
    }
}