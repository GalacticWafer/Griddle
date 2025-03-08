package com.galacticware.griddle.domain.view.composable.nestedappscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticware.griddle.domain.model.geometry.BoardEdge
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.create
import com.galacticware.griddle.domain.model.gesture.Gesture.Companion.changeUserSetting
import com.galacticware.griddle.domain.model.appsymbol.SettingsValue
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changeusersettings.ChangeUserSettingArgs
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.PopScreenStack
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.view.composable.AutoResizeText
import com.galacticware.griddle.domain.view.composable.FontSizeRange
import com.galacticware.griddle.domain.view.colorization.Hue

object UserSettingsScreen : NestedAppScreen() {
    override val displayNextToKeyboardEdge = BoardEdge.TOP
    private val displayToAppSymbol = mutableListOf<Pair<SettingsValue, AppSymbol>>()

    @Composable
    override fun Show() {
        WhileOnTop {
            var currentVibrationDisplay by remember {
                mutableStateOf(SettingsValue.VIBRATION)
            }.also { displayToAppSymbol.add(it.value to AppSymbol.CURRENT_VIBRATION_INTENSITY_DISPLAY) }

            var currentMinimumDragLengthDisplay by remember {
                mutableStateOf(SettingsValue.MIN_DRAG_LENGTH)
            }.also { displayToAppSymbol.add(it.value to AppSymbol.CURRENT_MINIMUM_DRAG_LENGTH_DISPLAY) }

            var currentTracingEnabledDisplay by remember {
                mutableStateOf(SettingsValue.GESTURE_TRACING)
            }.also { displayToAppSymbol.add(it.value to AppSymbol.CURRENT_TRACING_ENABLED_DISPLAY) }

            var currentTurboModeChoiceDisplay by remember {
                mutableStateOf(SettingsValue.TURBO_MODE)
            }.also { displayToAppSymbol.add(it.value to AppSymbol.CURRENT_TURBO_MODE_ENABLED_LABEL) }

            val operationsList = remember {
                listOf(
                    listOf(
                        Gesture.DUMMY_CLICK to AppSymbol.VIBRATION_LABEL,
                        changeUserSetting(GestureType.CLICK, ChangeUserSettingArgs.DecreaseVibrationAmplitude) to AppSymbol.DECREMENT,
                        changeUserSetting(GestureType.CLICK, ChangeUserSettingArgs.ToggleVibration) to currentVibrationDisplay,
                        changeUserSetting(GestureType.CLICK, ChangeUserSettingArgs.IncreaseVibrationAmplitude) to AppSymbol.INCREMENT,
                    ),
                    listOf(
                        Gesture.DUMMY_CLICK to AppSymbol.MINIMUM_DRAG_LENGTH_LABEL,
                        changeUserSetting(GestureType.CLICK, ChangeUserSettingArgs.DecreaseMinimumDragLength) to AppSymbol.DECREMENT,
                        Gesture.DUMMY_CLICK to currentMinimumDragLengthDisplay,
                        changeUserSetting(GestureType.CLICK, ChangeUserSettingArgs.IncreaseMinimumDragLength) to AppSymbol.INCREMENT,),
                    listOf(
                        Gesture.DUMMY_CLICK to AppSymbol.CURRENT_TRACING_ENABLED_LABEL,
                        changeUserSetting(GestureType.CLICK, ChangeUserSettingArgs.ToggleGestureTracing) to currentTracingEnabledDisplay,
                        Gesture.DUMMY_CLICK to AppSymbol.CURRENT_TURBO_MODE_ENABLED_LABEL,
                        changeUserSetting(GestureType.CLICK, ChangeUserSettingArgs.ToggleTurboMode) to currentTurboModeChoiceDisplay,
                    ),
                )
                }
            val context = LocalContext.current

            val backgroundColor = keyboardContext.keyboard.defaultTheme.primaryBackgroundColor
            val textColor = Hue.TAN.hex
            val borderColor = Hue.PURPLE.hex
            val textModifier = Modifier
                .background(backgroundColor)
                .border(1.dp, borderColor)

            var currentVibrationChoice by remember { mutableStateOf(currentVibrationDisplay.provider.provideValue()) }
            var currentVibrationValue by remember { mutableStateOf(currentVibrationDisplay.provider.provideValue()) }
            var currentMinimumDragLengthValue by remember {
                mutableStateOf(
                    currentMinimumDragLengthDisplay.provider.provideValue()
                )
            }
            var currentTracingEnabledValue by remember { mutableStateOf(currentTracingEnabledDisplay.provider.provideValue()) }
            var currentTurboModeChoiceValue by remember {
                mutableStateOf(
                    currentTurboModeChoiceDisplay.provider.provideValue()
                )
            }

            Column {
                operationsList.forEach { operationMap ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        operationMap.forEach { (gesture, symbol) ->

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .requiredWidth(keyboardContext.keyboard.primaryLayer.originalColWidth.dp)
                                    .requiredHeight(keyboardContext.keyboard.primaryLayer.originalRowHeight.dp)
                                    .clickable {
                                        val editorOperation = gesture.editorOperation
                                        editorOperation.executeOperation(
                                            keyboardContext.copy(
                                                gesture = gesture
                                            )
                                        )
                                        currentVibrationDisplay = SettingsValue.VIBRATION
                                        currentMinimumDragLengthDisplay =
                                            SettingsValue.MIN_DRAG_LENGTH
                                        currentTracingEnabledDisplay = SettingsValue.GESTURE_TRACING
                                        currentTurboModeChoiceDisplay = SettingsValue.TURBO_MODE
                                    }
                            ) {
                                val text = when (symbol) {
                                    is SettingsValue -> when (symbol) {
                                        SettingsValue.VIBRATION -> currentVibrationValue
                                        SettingsValue.MIN_DRAG_LENGTH -> currentMinimumDragLengthValue
                                        SettingsValue.TURBO_MODE -> currentTurboModeChoiceValue
                                        SettingsValue.GESTURE_TRACING -> currentTracingEnabledValue
                                    }

                                    is AppSymbol -> symbol.currentDisplayText
                                    else -> ""
                                }
                                val symbol1 = when (symbol) {
                                    is AppSymbol -> symbol
                                    is SettingsValue -> {
                                        displayToAppSymbol.find { it.first == symbol }?.second as AppSymbol
                                    }

                                    else -> null
                                }
                                val textSize =
                                    AppSymbol.getKnownSettingsItemSizeFor(context, symbol1!!)
                                AutoResizeText(
                                    knownSize = if (textSize == -1f) null else textSize,
                                    appSymbol = symbol1,
                                    text = text,
                                    maxLines = text.count { it == '\n' } + 1,
                                    modifier = textModifier.fillMaxSize(),
                                    fontSizeRange = FontSizeRange(
                                        min = 10.sp,
                                        max = 50.sp,
                                    ),
                                    color = textColor,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                currentVibrationChoice =
                                    currentVibrationDisplay.provider.provideValue()
                                currentVibrationValue =
                                    currentVibrationDisplay.provider.provideValue()
                                currentMinimumDragLengthValue =
                                    currentMinimumDragLengthDisplay.provider.provideValue()
                                currentTracingEnabledValue =
                                    currentTracingEnabledDisplay.provider.provideValue()
                                currentTurboModeChoiceValue =
                                    currentTurboModeChoiceDisplay.provider.provideValue()
                            }
                        }
                    }
                }
            }
        }
    }
}