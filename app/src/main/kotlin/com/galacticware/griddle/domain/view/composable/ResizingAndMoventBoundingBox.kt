package com.galacticware.griddle.domain.view.composable

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.KeyboardOffsetAndSize
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ResizingAndMovementBoundingBox(
    board: Keyboard,
    content: @Composable () -> Unit = {},
) {
    val context = LocalContext.current
    // Ensure there is a current layer before we begin
    Keyboard.currentLayer?: run {
        Keyboard.currentLayer = board.currentLayer()
    }
    var gridPosition by remember { mutableStateOf(Keyboard.offsetAndSize) }
    var isResizingAndMoving by remember { mutableStateOf(Keyboard.isResizingAndMoving) }

    LaunchedEffect(Unit) {
        gridPosition = Keyboard.offsetAndSize
    }

    var lastDragEndTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isDragging by remember { mutableStateOf(false) }
    val timer = remember { Timer() }
    val mainHandler = Handler(Looper.getMainLooper())
    var layoutCoordinates by remember { mutableStateOf(null as LayoutCoordinates?) }
    var offsetX by remember { mutableFloatStateOf(gridPosition.offsetX) }

    val getDragTimerTask = {
        object : TimerTask() {
            override fun run() {
                if (!isDragging && System.currentTimeMillis() - lastDragEndTime > 2000L) {
                    cancel()
                    timer.purge()
                    Keyboard.offsetAndSize = KeyboardOffsetAndSize(
                        offsetX = layoutCoordinates!!.positionInWindow().x,
                        width = board.width.value,
                        height = board.height.value,
                    )
                    board.saveBoardPositionAndSize(Keyboard.offsetAndSize)
                    mainHandler.post {
                        Toast.makeText(context, "Resizing & movement\nOFF!", Toast.LENGTH_LONG).show()
                    }
                    NestedAppScreen.stack.pop()
                    Keyboard.isResizingAndMoving = false
                }
            }
        }
    }

    var dragTimerTask by remember { mutableStateOf(getDragTimerTask()) }
    Box(modifier = Modifier.wrapContentWidth()) {
        val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels

        var modifier = Modifier
            .absoluteOffset { IntOffset(offsetX.roundToInt(), 0) }
            .background(Color.Transparent)
            .wrapContentSize()
            .align(Alignment.BottomEnd)
            .onGloballyPositioned {
                layoutCoordinates = it
                val absoluteOffset = it.positionOnScreen().x.roundToInt()
                if (it.size.width + absoluteOffset > screenWidth) {
                    val widthOffScreenToTheRight = it.size.width + absoluteOffset - screenWidth
                    offsetX -= widthOffScreenToTheRight
                } else if (absoluteOffset < 0) {
                    offsetX -= absoluteOffset
                }
            }


        if (isResizingAndMoving) {
            modifier = modifier
                .pointerInput(Unit) {
                    detectDragGestures (
                        onDragStart = {
                            isDragging = true
                            dragTimerTask.cancel()
                            timer.purge()
                        },
                        onDragEnd = {
                            isDragging = false
                            dragTimerTask = getDragTimerTask()
                            timer.schedule(dragTimerTask, 0, 100)
                        },
                        onDrag = { _, dragAmount ->
                            lastDragEndTime = System.currentTimeMillis()
                            val deltaX = dragAmount.x
                            val deltaY = dragAmount.y
                            val boardWidthPixels = board.width.toPx()
                            println("boardWidthPixels=$boardWidthPixels")
                            val availableHorizontalSpace = screenWidth - boardWidthPixels
                            val maxBoardWidth = context.resources.displayMetrics.widthPixels
                            val bw = board.width.toPx().toInt()
                            if(deltaY > 0 && bw >= maxBoardWidth - 5 || abs(deltaX) > abs(deltaY)) {
                                val spaceToTheLeft = layoutCoordinates?.positionInRoot()?.x ?: 0f
                                val minimumValue = -spaceToTheLeft
                                val maximumValue = screenWidth - boardWidthPixels

                                if (maximumValue < minimumValue) {
                                    if (deltaY > 0) {
                                        board.width = ((board.width.toPx() - deltaY).coerceAtLeast(
                                            board.minWidth.toFloat() * context.resources.displayMetrics.density
                                        )).toDp()
                                    }
                                    // Adjust width, height, and offset
                                    board.height = (board.width / board.defaultWidthToHeightAspectRatio)
                                    offsetX = 0f
                                    return@detectDragGestures
                                }

                                val isShiftingLeft = deltaX < 0
                                val isShiftingRight = deltaX > 0

                                layoutCoordinates?.let { lc ->
                                    val actualOffsetX = lc.positionOnScreen().x

                                    if (isShiftingLeft && actualOffsetX > 0) {
                                        offsetX -= min(5f, actualOffsetX)
                                    } else if (isShiftingRight && actualOffsetX + boardWidthPixels < screenWidth) {
                                        offsetX += min(5f, screenWidth - actualOffsetX - boardWidthPixels)
                                    }
                                }
                            } else if (abs(deltaY) > abs(deltaX)) {
                                board.width = ((board.width.toPx() - deltaY)
                                    .coerceAtLeast(
                                        board.minWidth.toFloat() * context.resources.displayMetrics.density
                                    )).toInt().toDp()
                                board.height = (board.width / board.defaultWidthToHeightAspectRatio)
                            }

                            if (availableHorizontalSpace < 0) {
                                // Adjust width, height, and offset
                                board.width = screenWidth.toDp()
                                board.height = (board.width / board.defaultWidthToHeightAspectRatio)
                                offsetX = 0f
                            }
                    })
                }
        }
        Box(modifier) {
            content()
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            isResizingAndMoving = Keyboard.isResizingAndMoving
            delay(20) // Adjust the delay as needed
        }
    }
}