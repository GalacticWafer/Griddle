package com.galacticware.griddle.domain.view.composable.nestedappscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import com.galacticware.griddle.domain.model.geometry.BoardEdge
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.operation.implementation.noargs.changeinputmethod.ChangeInputMethod
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import com.galacticware.griddle.domain.model.operation.implementation.noargs.resizeboard.ResizeBoard
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.PopScreenStack
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreenArgs.Companion.OpenAutoFixers
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreenArgs.Companion.OpenGriddleSetting
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreenArgs.Companion.OpenKeyboardDesigner
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreenArgs.Companion.OpenLanguagePreferences
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreenArgs.Companion.OpenTextReplacementEditor
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues
import com.galacticware.griddle.domain.model.usercontolled.VibrationChoice
import com.galacticware.griddle.domain.model.util.vibrate
import com.galacticware.griddle.domain.view.composable.AutoResizeText
import com.galacticware.griddle.domain.view.composable.FontSizeRange
import com.galacticware.griddle.domain.view.colorization.Hue

private val operations = mutableMapOf(
    PopScreenStack to AppSymbol.GO_BACK_ENGLISH,
    OpenAutoFixers to AppSymbol.AUTO_FIXERS,
    ResizeBoard to AppSymbol.RESIZE_BOARD,
    OpenGriddleSetting to AppSymbol.USER_CHANGEABLE_SETTINGS,
    OpenLanguagePreferences to AppSymbol.LANGUAGE_PREFERENCES,
    OpenKeyboardDesigner to AppSymbol.BUILD_YOUR_OWN_KEYBOARD,
    OpenTextReplacementEditor to AppSymbol.TEXT_REPLACEMENT_EDITOR,
    ChangeInputMethod to AppSymbol.CHOOSE_DIFFERENT_INPUT_METHOD,
)

@Serializable
object GlobalSettingScreen : NestedAppScreen() {
    override val displayNextToKeyboardEdge get() = BoardEdge.TOP
    override val addBackButton get() = false

    @Composable
    override fun Show() {
        WhileOnTop {
            val context = LocalContext.current
            val backgroundColor = keyboardContext.keyboard.defaultTheme.primaryBackgroundColor
            val iterator = operations.iterator()
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(backgroundColor)
//                .border(10.dp, Color.Red)
                    .absoluteOffset { IntOffset(0, 0) },
                columns = GridCells.Fixed(4),
            ) {
                val textColor = Hue.TAN.hex
                val borderColor = Hue.PURPLE.hex
                val textModifier = Modifier
                    .background(backgroundColor)
                    .border(1.dp, borderColor)
                items(operations.size) {
                    val (operation, symbol) = iterator.next()
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { layoutCoordinates ->
                                coordinates = layoutCoordinates
                            }
                            .requiredWidth(keyboardContext.keyboard.primaryLayer.originalColWidth.dp)
                            .requiredHeight(keyboardContext.keyboard.primaryLayer.originalRowHeight.dp)
                            .clickable {
                                if (UserDefinedValues.current.userVibration.toggledChoice == VibrationChoice.ON) {
                                    vibrate(keyboardContext.context)
                                }
                                when (operation) {
                                    is Operation -> operation.executeOperation(keyboardContext)
                                    is OperationArgs -> operation.opInstance().executeOperation(
                                        keyboardContext.copy(
                                            gesture = keyboardContext.gesture.withArgsJson(operation)
                                        )
                                    )
                                }
                            }
                    ) {
                        val text = symbol.value
                        val textSize = AppSymbol.getKnownSettingsItemSizeFor(context, symbol)
                        AutoResizeText(
                            knownSize = if (textSize == -1f) null else textSize,
                            appSymbol = symbol,
                            text = text,
                            maxLines = text.count { it == '\n' } + 1,
                            modifier = textModifier.fillMaxWidth(),
                            fontSizeRange = FontSizeRange(
                                min = 10.sp,
                                max = 50.sp,
                            ),
                            color = textColor,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

