package com.galacticware.griddle.domain.model.textreplacement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ImportExportTextReplacements(
    state: TextReplacementState,
    onEvent: (TextReplacementEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
//            onEvent(TextReplacementEvent.CancelImportingAndExportingTextReplacements)
        },
        title = { Text(text = "Import/Export TextReplacements") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.abbreviation,
                    onValueChange = {
                        onEvent(TextReplacementEvent.SetAbbreviation(it))
                    },
                    placeholder = {
                        Text(text = "Abbreviation")
                    }
                )
                TextField(
                    value = state.replacement,
                    onValueChange = {
                        onEvent(TextReplacementEvent.SetReplacement(it))
                    },
                    placeholder = {
                        Text(text = "Replacement")
                    }
                )
                // A check box to determine if the textReplacement requires whitespace before it
                Row {
                    Text(text = "Requires whitespace before")
                    Checkbox(
                        checked = state.requiresWhitespaceBefore,
                        onCheckedChange = {
                            onEvent(TextReplacementEvent.SetRequiredWhiteSpace(it))
                        },
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                Box(contentAlignment = Alignment.CenterStart) {
                    Button(onClick = {onEvent(TextReplacementEvent.CancelSavingTextReplacement)}) {
                        Text(text = "Cancel")
                    }
                }
                Box(contentAlignment = Alignment.CenterEnd) {
                    Button(onClick = { onEvent(TextReplacementEvent.SaveTextReplacement) }) {
                        Text(text = "Save")
                    }
                }
            }
        }
    )
}