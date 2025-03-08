package com.galacticware.griddle.domain.view.composable.nestedappscreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.model.util.PreferencesHelper.AUTO_CAPS
import com.galacticware.griddle.domain.model.util.PreferencesHelper.AUTO_CORRECTION
import com.galacticware.griddle.domain.model.util.PreferencesHelper.AUTO_PUNCTUATION
import com.galacticware.griddle.domain.view.colorization.Hue
import kotlinx.serialization.Serializable

@Serializable
object AutoFixersScreen : NestedAppScreen() {

    @Composable
    override fun Show() {
        WhileOnTop {
            val context = LocalContext.current
            val sharedPreferences = context.getSharedPreferences(
                PreferencesHelper.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()

            var autoCapitalization by remember {
                mutableStateOf(sharedPreferences.getBoolean(AUTO_CAPS, false))
            }
            var autoPeriod by remember {
                mutableStateOf(sharedPreferences.getBoolean(AUTO_PUNCTUATION, false))
            }
            var autoCorrection by remember {
                mutableStateOf(sharedPreferences.getBoolean(AUTO_CORRECTION, false))
            }

            val textColor = keyboardContext.keyboard.defaultTheme.primaryTextColor
            val backgroundColor = keyboardContext.keyboard.defaultTheme.primaryBackgroundColor
            val textModifier = Modifier.background(backgroundColor).border(1.dp, textColor)
            val checkBoxModifier = Modifier
                .background(Hue.PINK.hex)
            val fontSize = 30.sp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        modifier = checkBoxModifier,
                        checked = autoCapitalization,
                        onCheckedChange = {
                            autoCapitalization = it
                            editor.putBoolean(AUTO_CAPS, it).apply()
                        }
                    )
                    Text(
                        text = "Auto-Capitalization",
                        color = textColor,
                        modifier = textModifier,
                        fontSize = fontSize
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        modifier = checkBoxModifier,
                        checked = autoPeriod,
                        onCheckedChange = {
                            autoPeriod = it
                            editor.putBoolean(AUTO_PUNCTUATION, it).apply()
                        }
                    )
                    Text(
                        text = "Auto-Period",
                        color = textColor,
                        modifier = textModifier,
                        fontSize = fontSize,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        modifier = checkBoxModifier,
                        checked = autoCorrection,
                        onCheckedChange = {
                            autoCorrection = it
                            editor.putBoolean(AUTO_CORRECTION, it).apply()
                        }
                    )
                    Text(
                        text = "Auto-Correction",
                        color = textColor,
                        modifier = textModifier,
                        fontSize = fontSize,
                    )
                }
            }
        }
    }
}