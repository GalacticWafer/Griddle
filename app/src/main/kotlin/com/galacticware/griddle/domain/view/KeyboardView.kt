package com.galacticware.griddle.domain.view

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.galacticware.griddle.android.dagger.DefaultKeyboardFactory
import com.galacticware.griddle.android.dagger.KeyboardFactory
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.input.IMEService
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.findUnsupportedOperationsWithMessages
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues
import com.galacticware.griddle.domain.model.util.SpeechRecognitionDelegate
import com.galacticware.griddle.domain.view.composable.BuildCurrentLayer
import com.galacticware.griddle.domain.view.composable.ResizingAndMovementBoundingBox
import com.galacticware.griddle.domain.view.composable.nestedappscreen.ClipboardScreen
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.viewmodel.IMEServiceViewModel
import javax.inject.Inject
import kotlin.math.roundToInt

class KeyboardView  @Inject constructor(
    context: Context,
    private val keyboardFactory: KeyboardFactory,
) : AbstractComposeView(context) {

    private val keyboard: Keyboard by lazy {
        keyboardFactory.createKeyboard(context)
    }
    constructor(context: Context) : this(context, DefaultKeyboardFactory())

    constructor(context: Context, attrs: AttributeSet?) : this(context)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs)

    init {
        UserDefinedValues.initializeCurrent(context)
        initializeClipboardManagement(context)
        SpeechRecognitionDelegate.setRecognizer(context)
        AppSymbol.initializeSettingsItemSizes(context)
        findUnsupportedOperationsWithMessages(OperationTag.entries)
    }

    private fun initializeClipboardManagement(context: Context) {
        val manager = ContextCompat.getSystemService(
            context, ClipboardManager::class.java
        )!!
        manager.primaryClip?.let { ClipboardScreen.push(context) }
        manager.addPrimaryClipChangedListener {
            manager.primaryClip?.let { ClipboardScreen.push(context) }
        }
    }

    @Composable
    @SuppressLint("MutableCollectionMutableState", "RememberReturnType")
    override fun Content(
    ) {

        var screen by remember { mutableStateOf(NestedAppScreen.stack.peek()) }
        var offsetX by remember { mutableIntStateOf(Keyboard.offsetAndSize.offsetX.roundToInt()) }
        LaunchedEffect(Unit) {
            while (true) {
                screen = NestedAppScreen.stack.peek()
                delay(300) // Adjust the delay as needed
            }
        }

            Box(
                modifier = Modifier
                    .wrapContentSize()
                //                .background(Color.Green)
                ,
                contentAlignment = Alignment.BottomStart
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(0.dp)
                        //.border(1.dp, Color.Yellow)
                        .absoluteOffset { Keyboard.offsetAndSize.intOffset }
                        .onGloballyPositioned { lc ->
                            offsetX = lc.positionInRoot().x.roundToInt()
                        }
                        .absoluteOffset { IntOffset(0, 0) },
                ) {
                    Box(
                        Modifier
//                        .border(5.dp, Color.Cyan)
                        ,
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .absoluteOffset { IntOffset(-offsetX, 0) }
                                    .wrapContentHeight()
//                                                .border(1.dp, Color.Cyan)
                            ) {
                                if (screen != null) {
                                    screen?.provideKeyboardContext(
                                        KeyboardContext(
                                            keyboard,
                                            context,
                                            Gesture.DUMMY_CLICK,
                                            listOf(),
                                            null,
                                            null,
                                            GridPosition.originUnit,
                                        )
                                    )
                                    screen?.Show()
                                    if (screen?.displayNextToKeyboardEdge == null) {
                                        return
                                    }
                                }
                            }
//                    B()
                            ResizingAndMovementBoundingBox(keyboard) {
                                BuildCurrentLayer(keyboard, LocalContext.current)
                            }
                        }
                    }
                }

        }
    }

    companion object {
        var isVisible = true
    }
}