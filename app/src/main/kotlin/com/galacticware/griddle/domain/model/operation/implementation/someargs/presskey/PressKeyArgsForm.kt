package com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind


@Composable
fun PressKeyArgsForm(
    initialArgs: PressKeyArgs? = null,
    onSubmit: (PressKeyArgs) -> Unit
) {
    var keycode by remember { mutableStateOf(TextFieldValue(initialArgs?.keycode?.toString() ?: "")) }
    var respectShift by remember { mutableStateOf(initialArgs?.respectShift ?: false) }
    var overrideMetaState by remember { mutableStateOf(initialArgs?.overrideMetaState ?: false) }
    var modifierKeys by remember { mutableStateOf(initialArgs?.modifierKeys?.toList() ?: listOf<ModifierKeyKind>()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Keycode input field
        OutlinedTextField(
            value = keycode,
            onValueChange = { keycode = it },
            label = { Text("Keycode") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Respect Shift checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = respectShift,
                onCheckedChange = { respectShift = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Respect Shift")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Override Meta State checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = overrideMetaState,
                onCheckedChange = { overrideMetaState = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Override Meta State")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Modifier Keys selection (for simplicity, using checkboxes)
        Text("Modifier Keys:")
        ModifierKeyKind.entries.forEach { modifierKey ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = modifierKeys.contains(modifierKey),
                    onCheckedChange = {
                        modifierKeys = if (it) {
                            modifierKeys + modifierKey
                        } else {
                            modifierKeys - modifierKey
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(modifierKey.name)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                onSubmit(
                    PressKeyArgs(
                        keycode = keycode.text.toIntOrNull() ?: 0, // Fallback to 0 if not a valid number
                        respectShift = respectShift,
                        overrideMetaState = overrideMetaState,
                        modifierKeys = modifierKeys.toTypedArray()
                    )
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PressKeyArgsFormPreview() {
    PressKeyArgsForm(
        initialArgs = PressKeyArgs(
            keycode = 65,
            respectShift = true,
            overrideMetaState = false,
            modifierKeys = arrayOf(ModifierKeyKind.SHIFT, ModifierKeyKind.CONTROL)
        ),
        onSubmit = { args ->
            println(args)
        }
    )
}