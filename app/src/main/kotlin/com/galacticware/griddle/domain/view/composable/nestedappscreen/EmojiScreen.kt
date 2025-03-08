package com.galacticware.griddle.domain.view.composable.nestedappscreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticware.griddle.domain.model.emojis.Emoji
import com.galacticware.griddle.domain.model.geometry.BoardEdge
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import java.io.IOException
import java.io.InputStream

object EmojiScreen : NestedAppScreen() {
    private const val EMOJI_BUTTON_WIDTH = 32
    private const val EMOJI_BUTTON_HEIGHT = 32
    private const val NUM_ROWS_PER_CARD = 6

    @Composable
    override fun Show() {
        WhileOnTop {
            val toList = emojiCards.entries.toList()
            LazyColumn(
                Modifier.requiredHeight(
                    keyboardContext.context.resources.displayMetrics.let {
                        it.heightPixels / it.density
                    }.dp.times(.45f)
                ),
            ) {
                items(toList.size) { i ->
                    val emojiCard = toList[i]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight((NUM_ROWS_PER_CARD.toFloat() * EMOJI_BUTTON_HEIGHT).dp)
                    ) {
                        LazyHorizontalGrid(
                            rows = GridCells.Fixed(NUM_ROWS_PER_CARD),
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(emojiCard.value.size) { i ->
                                val emoji = emojiCard.value[i]
                                Text(
                                    modifier = Modifier
                                        .requiredWidth(EMOJI_BUTTON_WIDTH.dp)
                                        .requiredHeight(EMOJI_BUTTON_HEIGHT.dp)
                                        .border(1.dp, Color.Black)
                                        .background(Color.White)
                                        .clickable {
                                            keyboardContext.inputConnection.commitText(emoji.codepoints)
                                        }
                                        .padding(0.dp),
                                    text = emoji.codepoints,
                                    fontSize = 25.sp,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.requiredHeight(10.dp))
                }
            }
        }
    }

    private const val ASSETS_ALL_EMOJIS_TXT_FILE = "all_emojis.csv"
    private val emojiCards by lazy { readEmojisFromFile() }

    private fun loadAssetFile(context: Context, fileName: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun convertUnicodeToText(unicodeString: String): String {
        val bytes = unicodeString.toByteArray(Charsets.UTF_8)
        return String(bytes, Charsets.UTF_8)
    }

    private fun readEmojisFromFile(s: String = ASSETS_ALL_EMOJIS_TXT_FILE) = run {
        loadAssetFile(keyboardContext.keyboard.context!!, s)
            ?.let { convertUnicodeToText(it) }
            ?.split("\\n".toRegex())
            ?.drop(1)
            ?.map { line ->
                val split = line.split(",".toRegex())
                Emoji(
                    name = split[0],
                    type = split[1],
                    codepoints = split[2],
                    description = split[3]
                )
            }?.groupBy { it.type }!!
            // for now get rid of the few erroneous categories
            .filter { it.value.size > 16 }
    }
}