package com.galacticware.griddle.domain.view.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.geometry.CartesianAxis
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_PRIMARY_THEME
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_SECONDARY_THEME

/**
 * The base font size ratio defines the font size that looks good on a specific DP.
 * We can then use this as the ratio for other font sizes by checking the DP of the board on a different device.
 */
const val BASE_FONT_SIZE_RATIO = 23.0 / 700
@Composable
fun BoxScope.PutThemeAndGesturePairs(
    themeAndGesturePairs: List<Pair<ModifierTheme, Gesture>>,
    gestureButton: GestureButton,
    isTurboModeEnabled: Boolean,
    localDensity: Density,
    board: Keyboard,
) {
    themeAndGesturePairs.forEach { (theme, gesture) ->
        val gestureType = GestureType.fromInstance(gesture)
        if(gestureType !in GestureType.visibleTypes) return@forEach

        val isCenterLegend = gestureType == GestureType.CLICK
        val isIndicatorLegend = gesture.currentAssignment.isIndicator

        val currentText = gesture.currentAssignment.appSymbol?.currentDisplayText
            ?:theme.text
            ?: gesture.currentText

        if(isTurboModeEnabled && !isCenterLegend && !isIndicatorLegend &&
            currentText.any { !it.isLetter() }) return@forEach

        if(currentText.isEmpty()) return@forEach

        val alignment: Alignment = ModifierTheme.alignment(gestureType)
        val rectangleDefinition = gesture.gridRectangleDefinition
        val shrinkageMultiplier = 1f / (currentText.length - 1).coerceAtLeast(1)
        val centerLegendMultiplier = if(isCenterLegend) 3f else 1.5f
        val baseFontSize = board.width.value * BASE_FONT_SIZE_RATIO
        val assumedFontSize = shrinkageMultiplier * baseFontSize * centerLegendMultiplier

        val textColor = if (isIndicatorLegend)
            theme.primaryTextColor
        else if(isCenterLegend)
            DEFAULT_PRIMARY_THEME.primaryTextColor
        else
            DEFAULT_SECONDARY_THEME.primaryTextColor
        Text(
            text = currentText,
            modifier = Modifier
                .wrapContentSize()
                .padding(3.dp)
                .align(alignment)
                .border(.5.dp, theme.primaryBorderColor)
                .background(theme.primaryBackgroundColor)
                .sizeIn(minWidth = 1.dp, minHeight = 1.dp)
                .graphicsLayer {
                    gesture.assignment.appSymbol?.mirrorAxis?.let {
                        when(it) {
                            CartesianAxis.X -> scaleY = -1f
                            CartesianAxis.Y -> scaleX = -1f
                        }
                    }
                }
            ,
            textAlign = ModifierTheme.textAlignment(rectangleDefinition),
            fontSize = assumedFontSize.sp,
            color =
            textColor,
        )
    }
}
