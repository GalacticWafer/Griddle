package com.galacticware.griddle.domain.view.composable.nestedappscreen

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.Keyboard.Companion.clearScreenStack
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.model.textreplacement.AddTextReplacementDialog
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementEvent
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementListOperation
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementListSortType
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementState
import com.galacticware.griddle.domain.view.colorization.Hue
import com.galacticware.griddle.domain.view.navigation.GriddleAppNavigation
import java.util.Timer
import java.util.TimerTask

@Serializable
object TextReplacementEditorScreen: NestedAppScreen() {
    override val displayNextToKeyboardEdge = null
    var isTextReplacementBlocked = true
    @Composable
    override fun Show() {
        Show(
            onEvent = GriddleAppNavigation.textReplacementViewModel!!::onEvent,
            state = GriddleAppNavigation.textReplacementViewModel!!.state.collectAsState().value,
        )
    }
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Show(
        state: TextReplacementState,
        onEvent: (TextReplacementEvent) -> Unit
    ) {
        WhileOnTop {
            if (isTextReplacementBlocked) {
                Keyboard.textReplacementScreenBlocker = this::class
                isTextReplacementBlocked = false
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .requiredHeight(
                        keyboardContext.context.resources.displayMetrics.let {
                            it.heightPixels / it.density
                        }.dp.times(.8f)
                    ),
            ) {
                // Search bar
                val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }
                Row {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            setSearchQuery(it)
                            onEvent(TextReplacementEvent.UpdateSearchQuery(it))
                        },
                        label = { Text("Search Text Replacements") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.fillMaxWidth()
                    .requiredHeight(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
                )
                Row(
                    modifier = Modifier
                        .border(2.dp, Hue.YELLOW.hex)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),

                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextReplacementListSortType.entries.forEach { sortType ->
                        Row(
                            modifier = Modifier.clickable {
                                onEvent(TextReplacementEvent.SortTextReplacements(sortType))
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType,
                                onClick = {
                                    onEvent(TextReplacementEvent.SortTextReplacements(sortType))
                                }
                            )
                            Text(
                                text = sortType.text,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.fillMaxWidth()
                    .requiredHeight(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .border(2.dp, Hue.PURPLE.hex)
                ) {
                    Button(onClick = {
                        Keyboard.textReplacementScreenBlocker = null
                        Timer().schedule(object : TimerTask() { override fun run() {
                            isTextReplacementBlocked = true
                        } }, 500)
                        stack.pop()
                            ?: run { clearScreenStack() }
                    }) {
                        Text("Back")
                    }
                    Button(onClick = {
                        onEvent(TextReplacementEvent.AddTextReplacement)
                    }) {
                        Text("Add Text Replacement")
                    }
                    /*Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        Button(
                            onClick = {
                                onEvent(TextReplacementEvent.ExportTextReplacements)
                            }
                        ) {
                            Text(
                                text = "Export TextReplacements",
                                fontSize = 12.sp,
                            )
                        }
                    }*/
                }
                Spacer(modifier = Modifier.fillMaxWidth()
                    .requiredHeight(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
                )

                Box(modifier = Modifier
                    .padding(start = 20.dp, end = 10.dp)
//                .border(2.dp, AppColor.ORANGE.color)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .border(2.dp, Color.Black)
//                        .padding(1.dp)
                        ,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.textReplacements.filter {
                            when (state.sortType) {
                                TextReplacementListSortType.ABBREVIATION -> it.abbreviation
                                TextReplacementListSortType.REPLACEMENT -> it.replacement
                                TextReplacementListSortType.WHITESPACE_INSENSITIVE -> it.requiresWhitespaceBefore.toString()
                            }.contains(searchQuery, ignoreCase = true)
                        }) { textReplacement ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .requiredHeight(40.dp)
                                        .pointerInteropFilter { motionEvent ->
                                            if (motionEvent.pressure == 0f || motionEvent.action != MotionEvent.ACTION_UP) {
                                                // this is probably a hover event
                                                return@pointerInteropFilter true
                                            }
                                            onEvent(TextReplacementEvent.SetAbbreviation(textReplacement.abbreviation))
                                            onEvent(TextReplacementEvent.SetReplacement(textReplacement.replacement))
                                            state.isAddingTextReplacement = TextReplacementListOperation.SAVE
                                            onEvent(TextReplacementEvent.AddTextReplacement)
                                            true
                                        }
                                ) {
                                    Text(textReplacement.abbreviation)
                                }
                                Box(modifier = Modifier.wrapContentSize()) {
                                    Text(text = "   →   ")
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .requiredHeight(40.dp)
                                        .pointerInteropFilter { motionEvent ->
                                            if (motionEvent.pressure == 0f || motionEvent.action != MotionEvent.ACTION_UP) {
                                                // this is probably a hover event
                                                return@pointerInteropFilter true
                                            }
                                            onEvent(TextReplacementEvent.SetAbbreviation(textReplacement.abbreviation))
                                            onEvent(TextReplacementEvent.SetReplacement(textReplacement.replacement))
                                            state.isAddingTextReplacement = TextReplacementListOperation.SAVE
                                            onEvent(TextReplacementEvent.AddTextReplacement)
                                            true
                                        }
                                ) {
                                    Text(textReplacement.replacement)
                                }
                                IconButton(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    onClick = {
                                        onEvent(TextReplacementEvent.ShowDeletionConfirmation(textReplacement.abbreviation))
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete textReplacement"
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.fillMaxWidth()
                    .requiredHeight(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
                )
                when (state.isAddingTextReplacement) {
                    TextReplacementListOperation.SAVE -> AddTextReplacementDialog(state, onEvent)
                    TextReplacementListOperation.DELETE -> DeleteTextReplacementDialog(state, onEvent, applicationContext)
                    null -> {}
                }
            }
        }
    }

    private @Composable
    fun DeleteTextReplacementDialog(
        state: TextReplacementState,
        onEvent: (TextReplacementEvent) -> Unit,
        context: Context = LocalContext.current
    ) {
        AlertDialog(
            onDismissRequest = {
                onEvent(TextReplacementEvent.HideDeletionConfirmation)
            },
            title = { Text("Delete TextReplacement") },
            text = { Text("Are you sure you want to delete this textReplacement?") },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(TextReplacementEvent.DeleteTextReplacement(context))
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onEvent(TextReplacementEvent.HideDeletionConfirmation)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}