package com.galacticware.griddle.domain.view.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.galacticware.griddle.domain.model.screen.NestedAppScreen.Companion.stack

@Composable
fun BackButton(
    label: String,
    function: (() -> Unit)? = null
) {
    Box(Modifier.wrapContentSize()) {
        Button(
            modifier = Modifier.align(Alignment.BottomStart),
            onClick = {
                stack.pop()
                function?.invoke()
            }
        ) {
            Text(label)
        }
    }
}