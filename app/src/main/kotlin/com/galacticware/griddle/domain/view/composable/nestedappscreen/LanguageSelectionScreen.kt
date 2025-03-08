package com.galacticware.griddle.domain.view.composable.nestedappscreen

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.view.colorization.Hue
import com.galacticware.griddle.domain.view.navigation.GriddleAppNavigation

@Serializable
object LanguageSelectionScreen: NestedAppScreen() {
    // Store the list of integers
    @Composable
    private fun storeIntegerList(context: Context, key: String, intList: List<Int>) {
        val prefs = context.getSharedPreferences("your_prefs_name", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val stringSet = intList.map { it.toString() }.toSet()
        editor.putStringSet(key, stringSet)
        editor.apply()
    }

    // Retrieve the list of integers
    @Composable
    fun getIntegerList(context: Context, key: String): List<Int> {
        val prefs = context.getSharedPreferences("your_prefs_name", Context.MODE_PRIVATE)
        val stringSet = prefs.getStringSet(key, emptySet())
        return stringSet?.map { it.toInt() } ?: emptyList()
    }

    private const val PREFS_NAME = "user_prefs"

    @Composable
    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Composable
    fun setPrimaryLanguageComposable(context: Context, languageTag: LanguageTag) {
        getPreferences(context).edit().putInt("primary_language", languageTag.ordinal).apply()
    }

    @Composable
    fun getAllLanguagesComposable(context: Context): Set<LanguageTag> {
        val languageOrdinals = getIntegerList(context, "language_preferences")
        return languageOrdinals.mapNotNull { LanguageTag.entries.getOrNull(it) }.toSet()
    }

    @Composable
    fun addLanguageComposable(context: Context, languageTag: LanguageTag) {
        val languageOrdinals = getIntegerList(context, "language_preferences").toMutableList()
        languageOrdinals.add(languageTag.ordinal)
        storeIntegerList(context, "language_preferences", languageOrdinals)
    }

    @Composable
    fun removeLanguageComposable(languageTag: LanguageTag) {
        val context = LocalContext.current
        val languageOrdinals = getIntegerList(context, "language_preferences").toMutableList()
        languageOrdinals.remove(languageTag.ordinal)
        storeIntegerList(context, "language_preferences", languageOrdinals)
    }

    val languageTagList: List<LanguageTag> = mutableListOf()
    lateinit var currentOnKeyboardSelected: (LanguageTag) -> Unit
    lateinit var currentOnPrimaryKeyboardLanguageChanged: (LanguageTag) -> Unit
    lateinit var currentOnRemoveKeyboard: (LanguageTag) -> Unit
    lateinit var currentOnDone: (List<LanguageTag>, Context) -> Unit

    fun setFuncs(
        onKeyboardSelected: (LanguageTag) -> Unit,
        onPrimaryKeyboardLanguageChanged: (LanguageTag) -> Unit,
        onRemoveKeyboard: (LanguageTag) -> Unit,
        onDone: (List<LanguageTag>, Context) -> Unit,
    ) {
        currentOnKeyboardSelected = onKeyboardSelected
        currentOnPrimaryKeyboardLanguageChanged = onPrimaryKeyboardLanguageChanged
        currentOnRemoveKeyboard = onRemoveKeyboard
        currentOnDone = onDone
    }

    @Composable
    override fun Show() {
        WhileOnTop {
            val context = LocalContext.current
            show(
                onPrimaryKeyboardLanguageChanged = currentOnPrimaryKeyboardLanguageChanged,
                onRemoveKeyboard = currentOnRemoveKeyboard,
                onDone = currentOnDone,
                context = context
            )
        }
    }

    @Composable
     private fun show(
        onPrimaryKeyboardLanguageChanged: (LanguageTag) -> Unit,
        onRemoveKeyboard: (LanguageTag) -> Unit,
        onDone: (List<LanguageTag>, Context) -> Unit,
        context: Context = LocalContext.current,
        navController: NavController? = null
    ) {

        var primaryLanguage by remember { mutableStateOf(PreferencesHelper
            .getUserPrimaryLanguage(context)) }
        
        var selectedLanguages by remember { mutableStateOf(PreferencesHelper
            .getUserPreferredLanguages(context)
            .filter { it != primaryLanguage }) }
        
        LaunchedEffect(Unit) {
            primaryLanguage = PreferencesHelper.getUserPrimaryLanguage(context)
            selectedLanguages = PreferencesHelper.getUserPreferredLanguages(context)
                .filter { it != primaryLanguage }
                .toMutableList()

            delay(100)
        }

        var showAlert by remember { mutableStateOf(false) }
        var newLanguage by remember { mutableStateOf<LanguageTag?>(null) }
        var searchQuery by remember { mutableStateOf("") }

        if (showAlert && newLanguage != null) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                title = { Text("Select Language to Replace") },
                text = {
                    Column {
                        selectedLanguages.forEach { language ->
                            TextButton(
                                onClick = {
                                    selectedLanguages = selectedLanguages.minus(language)
                                    showAlert = false
                                }
                            ) {
                                Text(language.name)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAlert = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                modifier = Modifier
                    .border(4.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
                    .padding(8.dp),
                text = "Select Keyboard Layers",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
            )
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search keyboards...") }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.8f)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .border(4.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Select Languages",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface.let {
                        if (isSystemInDarkTheme()) Color.White else Color.Black
                    }
                )
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    val filteredLanguages = LanguageTag.entries.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }
                    items(filteredLanguages.size) { i ->
                        LanguageItem(
                            languageTag = filteredLanguages[i],
                            isPrimaryLanguage = filteredLanguages[i] == primaryLanguage,
                            isSelected = selectedLanguages.contains(filteredLanguages[i]),
                            onLanguageSelected = { languageTag ->
                                if (selectedLanguages.size < 3) {
                                    selectedLanguages = selectedLanguages.plus(languageTag)
                                } else {
                                    newLanguage = languageTag
                                    showAlert = true
                                }
                            },
                            onPrimaryLanguageChanged = onPrimaryKeyboardLanguageChanged,
                            onRemoveLanguage = { languageTag ->
                                if (selectedLanguages.size > 1) {
                                    selectedLanguages = selectedLanguages.minus(languageTag)
                                    onRemoveKeyboard(languageTag)
                                }
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            onDone(selectedLanguages.toList(), context)
                            val joinToString = selectedLanguages.filter { !it.isImplemented }
                                .joinToString(",\n- ") { it.name }
                            GriddleAppNavigation.isWarningAlertShown = joinToString.isNotEmpty()
                            GriddleAppNavigation.notImplementedLanguagesStringList =
                                joinToString
                            navController
                                ?.popBackStack()
                                ?: run { stack.pop() }
                        }
                    ) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Done",
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun LanguageItem(
        languageTag: LanguageTag,
        isPrimaryLanguage: Boolean,
        isSelected: Boolean,
        onLanguageSelected: (LanguageTag) -> Unit,
        onPrimaryLanguageChanged: (LanguageTag) -> Unit,
        onRemoveLanguage: (LanguageTag) -> Unit,
    ) {
        val backgroundColor =
            if(isPrimaryLanguage) MaterialTheme.colorScheme.primary
            else if (isSelected) Hue.ORANGE.hex
            else MaterialTheme.colorScheme.background
        val textColor =
            if(isPrimaryLanguage) MaterialTheme.colorScheme.onPrimary
            else if (isSelected) Color.Black
            else MaterialTheme.colorScheme.onSurface

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(backgroundColor)
                .clickable {
                    if (!isSelected) {
                        onLanguageSelected(languageTag)
                    } else {
                        onRemoveLanguage(languageTag)
                    }
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = languageTag.name,
                color = textColor,
                modifier = Modifier.padding(8.dp)
            )
            if (isPrimaryLanguage) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Primary Language",
                    tint = textColor
                )
            }
        }
    }
}