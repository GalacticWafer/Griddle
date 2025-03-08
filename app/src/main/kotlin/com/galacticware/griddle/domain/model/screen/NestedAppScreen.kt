package com.galacticware.griddle.domain.model.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import com.galacticware.griddle.domain.model.collection.ConcurrentStack
import com.galacticware.griddle.domain.model.geometry.BoardEdge
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.view.composable.BackButton
import kotlinx.coroutines.delay

abstract class NestedAppScreen : DisplayNestedScreen {
    override val displayNextToKeyboardEdge: BoardEdge? = null
    protected lateinit var keyboardContext: KeyboardContext
    protected val applicationContext get() = keyboardContext.context
    protected val defaultTheme get() = keyboardContext.keyboard.defaultTheme
    protected val backgroundColor get() = defaultTheme.primaryBackgroundColor
    protected val borderColor get() = defaultTheme.primaryBorderColor
    protected val textColor get() = defaultTheme.primaryTextColor
    open val addBackButton = true
    var coordinates: LayoutCoordinates? = null


    @Composable
    protected fun WhileOnTop(content: @Composable () -> Unit) {
        var screen by remember { mutableStateOf(null as NestedAppScreen?) }
        if (stack.peek() != this) return
        screen = stack.peek()
        if (screen != null) {
            LaunchedEffect(Unit) {
                while (true) {
                    delay(5)
                    screen = stack.peek()
                }
            }
        }
        Column {
            if (addBackButton) {
                BackButton("Back")
            }
            content.invoke()
        }
    }

    @Composable
    override fun Show() {
        throw NotImplementedError("Please implement Show() in ${this::class.simpleName}")
    }


    fun provideKeyboardContext(keyboardContext: KeyboardContext) {
        this.keyboardContext = keyboardContext
    }
    companion object {
        val stack: ConcurrentStack<NestedAppScreen> = ConcurrentStack()
    }
}