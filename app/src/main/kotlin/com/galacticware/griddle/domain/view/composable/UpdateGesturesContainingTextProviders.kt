package com.galacticware.griddle.domain.view.composable

import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.modifier.ModifierTheme


@Composable
fun UpdateGesturesContainingTextProviders(hashMap: Map.Entry<Pair<Int, Int>, Pair<GestureButton, List<Pair<ModifierTheme, Gesture>>>>) =
    hashMap.value.let { (gestureButton, themeAndGesturePairs) ->
        gestureButton to themeAndGesturePairs.map { (theme, gesture) ->
            val provider = gestureButton.settingsValueProvider
            val text = provider?.provideValue()
            if (text?.contains("BSS") == true) {
                println()
            }
            theme.withText(text ?: theme.text ?: gesture.currentText) to gesture
        }
    }