package com.galacticware.griddle.domain.view.composable.nestedappscreen

import android.content.ClipboardManager
import android.content.Context
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticware.griddle.domain.model.clipboard.ClipDataQueue
import com.galacticware.griddle.domain.model.clipboard.GriddleClipboardItem
import com.galacticware.griddle.domain.model.input.GriddleInputConnection
import com.galacticware.griddle.domain.model.input.IMEService
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.view.colorization.Hue

object ClipboardScreen: NestedAppScreen() {


    @Composable
    override fun Show() {
        WhileOnTop {
            val clipDataItems = clipDataQueue.items

            var floatingButtonSideLength: Int =
                (applicationContext.resources.displayMetrics.let { 180.dp.value / it.density }).toInt()
            val scollableHeight: Dp =
                (applicationContext.resources.displayMetrics.let { it.heightPixels / it.density } * .65).let { halfScreenHeight ->
                    (halfScreenHeight - floatingButtonSideLength).dp
                }
            val numItems = clipDataItems.size
            val height =
                (3 * floatingButtonSideLength * (numItems / 3 + if (numItems % 3 == 0) 0 else 1)).dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, scollableHeight)
                //                .border(width = 4.dp, color = Color.Green)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(1.dp, color = Hue.MEOK_DEFAULT_YELLOW.hex)
                    //                    .border(width = 6.dp, color = Color.Blue)
                ) {
                    Button(
                        onClick = { stack.pop() },
                    ) {
                        Text(AppSymbol.GO_BACK_ENGLISH.value)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .heightIn(floatingButtonSideLength.dp, height)
                    //                    .border(width = 6.dp, color = Color.Red)
                ) {
                    Box {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3)
                        ) {
                            items(clipDataItems.size) { i ->
                                val item = clipDataItems[i]
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .heightIn(0.dp, 100.dp)
                                        //                                    .border(8.dp, AppColor.yellowBackground.primaryBackgroundColor)
                                        .border(1.dp, Color.White)
                                        .background(Hue.MEOK_DARK_GRAY.hex)
                                        .padding(2.dp)
                                        .clickable {
                                            invokeScrollableActionWithItem(item)
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(/*layer.colWidth.toFloat()*/)
                                            .wrapContentHeight()
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight(),
                                            text = item.toString(),
                                            fontSize = 20.sp,
                                            color = textColor,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun invokeScrollableActionWithItem(item: GriddleClipboardItem) {
        val aContext = keyboardContext.context
        val clipboardManager = aContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(item.data)
        GriddleInputConnection(
            (aContext as IMEService).currentInputConnection,
            aContext
        ).pressKey(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PASTE))
        keyboardContext.keyboard.switchToPreviousLayer()
    }
    fun push(context: Context) {
        GriddleClipboardItem(context).let {
            if(!it.isImage) clipDataQueue.push(it)
        }
    }

    var wasLastOperationSelectAll = false
    val clipDataQueue: ClipDataQueue = ClipDataQueue()

}