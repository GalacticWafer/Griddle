package com.galacticware.griddle.domain.view.composable

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import splitties.systemservices.inputMethodManager


@Serializable
object StartScreen {

    @Composable
    fun Show() {
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

        LaunchedEffect(key1 = keyboardHeight) {
            coroutineScope.launch {
                scrollState.scrollBy(keyboardHeight.toFloat())
            }
        }

        Column(
            Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            val ctx = LocalContext.current

            Row {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Griddle Keyboard",
                        modifier = Modifier.align(Alignment.CenterStart),
                    )
                    // second text is to align the text to the right
                    Text(
                        text = "App Version: ${
                            ctx.packageManager.getPackageInfo(
                                ctx.packageName, 0
                            ).versionName
                        }",
                        modifier = Modifier.align(Alignment.CenterEnd),
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            var isGriddleKeyboardEnabled by remember {
                mutableStateOf(
                    inputMethodManager.enabledInputMethodList
                        .any { it.packageName == ctx.packageName }
                )
            }

            var isGriddleKeyboardActive: Boolean by remember {
                mutableStateOf(isGriddleKeyboardEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                        && inputMethodManager.currentInputMethodInfo
                    ?.packageName == ctx.packageName)
            }

            MakeGlowingButton(ctx,
                isOkay = { ctx: Context -> isGriddleKeyboardEnabled },
                label = "First, click here to enable Griddle" to "Griddle is enabled!",
                function =  {
                    ctx.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                    Thread {
                        do { Thread.sleep(100) }
                        while (!(ctx as Activity).hasWindowFocus())
                        isGriddleKeyboardEnabled = inputMethodManager.enabledInputMethodList
                            .any { it.packageName == ctx.packageName }
                    }.start()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            MakeGlowingButton(ctx,
                isOkay = { ctx: Context -> isGriddleKeyboardActive },
                label = "${if(isGriddleKeyboardEnabled) "Now" else "Then"} click here to select Griddle as the input method" to "Griddle is selected!",
                function =  {
                    inputMethodManager.showInputMethodPicker()
                    Thread {
                        do { Thread.sleep(100)
                        } while (!(ctx as Activity).hasWindowFocus())
                        isGriddleKeyboardEnabled = inputMethodManager.enabledInputMethodList
                            .any { it.packageName == ctx.packageName }
                        isGriddleKeyboardActive = isGriddleKeyboardEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                                && inputMethodManager.currentInputMethodInfo
                            ?.packageName == ctx.packageName
                    }.start()
                }
            )

            LaunchedEffect(Unit) {
                while (true) {
                    delay(100)
                    isGriddleKeyboardEnabled = inputMethodManager.enabledInputMethodList
                        .any { it.packageName == ctx.packageName }
                    isGriddleKeyboardActive = isGriddleKeyboardEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                            && inputMethodManager.currentInputMethodInfo?.packageName == ctx.packageName
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            var isFocused by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                ,
                contentAlignment = Alignment.Center
                ,
            ) {
                Text(
                    text =
                    if(isFocused && isGriddleKeyboardActive && isGriddleKeyboardEnabled)
                        "Press-and-hold the settings gear (${AppSymbol.GLOBAL_SETTINGS.value}) for more settings."
                    else if (isGriddleKeyboardActive && isGriddleKeyboardEnabled)
                        "Tap below to try the keyboard"
                    else
                        "Please enable and select the Griddle keyboard"
                    ,
                    fontSize = 24.sp
                    ,
                    textAlign = TextAlign.Center,
                )
            }

            val textFieldValueState = remember {
                mutableStateOf(TextFieldValue(text = "Tap here to try the keyboard"))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(10.dp)
                ,
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        }
                        .fillMaxHeight()
                    ,
                    value = textFieldValueState.value,
                    onValueChange = { tfv ->
                        textFieldValueState.value = tfv
                    },
                    minLines = 5,
                    maxLines = 5,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            (LocalContext.current as Activity).let { activity ->
                if (ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.RECORD_AUDIO), 1
                    )
                }
            }
        }
    }

    @Composable
    private fun MakeOptionsButton(s: String, function: () -> Unit) {
        Button(modifier = Modifier.fillMaxWidth(), onClick = function) {
            Text(text = s)
        }
    }

    @Composable
    private fun MakeGlowingButton(
        ctx: Context, isOkay: (Context) -> Boolean, label: Pair<String, String>,
        function: () -> Unit
    ) {
        val isOk = !isOkay(ctx)
        val infiniteTransition = rememberInfiniteTransition(label = "pulsing_button_$label")
        val pulse by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulsing_button_$label}"
        )
        if (isOk) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                //
                colors = ButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.Red.copy(alpha = pulse),
                    disabledContentColor = MaterialTheme.colorScheme.surfaceDim,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                onClick = function
            ) {
                Text(
                    text = label.first,
                    modifier = Modifier.padding(4.dp),
                    fontSize = 24.sp
                )
            }
        } else {
            MakeOptionsButton(label.second) { function() }
        }
    }
}

