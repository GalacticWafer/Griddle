package com.galacticware.griddle.domain.view.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementDatabase
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementDao
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementViewModel
import com.galacticware.griddle.domain.model.usercontolled.UserLanguageSelector
import com.galacticware.griddle.domain.view.composable.nestedappscreen.AutoFixersScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.BuildYourOwnKeyboardScreen
import com.galacticware.griddle.domain.view.composable.nestedappscreen.LanguageSelectionScreen
import com.galacticware.griddle.domain.view.composable.ColorPicker
import com.galacticware.griddle.domain.view.composable.nestedappscreen.TextReplacementEditorScreen
import com.galacticware.griddle.domain.view.composable.StartScreen

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop all tables
        database.execSQL("DROP TABLE IF EXISTS text_replacers")
        // Check if the textReplacements table exists and rename it to text_replacers
        val cursor = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='textReplacements'")
        if (cursor.count > 0) {
            database.execSQL("ALTER TABLE textReplacements RENAME TO text_replacers")
        }
    }
}

class GriddleAppNavigation(
    private val navController: NavHostController,
    private val context: Context,
) {
    private val db by lazy {
        Room.databaseBuilder(
            navController.context.applicationContext,
            TextReplacementDatabase::class.java, "textReplacement-database"
        )
            //.fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_5_6).build()
    }
    val textReplacementViewModel: TextReplacementViewModel = run {
        val dao = db.textReplacementScreenDao
        textReplacementDao = dao
        TextReplacementViewModel(dao, context = context)
    }.also {
        Thread { runBlocking {
            it.reloadTextReplacements()
        } }.start()
    }.apply {
        Companion.textReplacementViewModel = this
    }
    companion object {
        var textReplacementViewModel: TextReplacementViewModel? = null
        var isWarningAlertShown = false
        var textReplacementDao: TextReplacementDao? = null
        var notImplementedLanguagesStringList = ""
    }
    @Composable
    fun Navigate(
        context: Context = LocalContext.current,
    ) {
        var notImplementedLanguagesStringList by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            notImplementedLanguagesStringList = GriddleAppNavigation.notImplementedLanguagesStringList
            delay(100)
        }

        LanguageSelectionScreen.setFuncs(
            onKeyboardSelected = {
                UserLanguageSelector.incomingLanguageLayerUpdate =
                    LanguageSelectionScreen.languageTagList to null },
            onPrimaryKeyboardLanguageChanged = { languageTag -> UserLanguageSelector.primaryLanguageTag = languageTag },
            onRemoveKeyboard = { languageTag -> UserLanguageSelector.languageTags.remove(languageTag) },
            onDone = { languageTagList, ctx ->
                UserLanguageSelector.incomingLanguageLayerUpdate = languageTagList to ctx
                UserLanguageSelector.setUserPreferredLanguages(
                    ctx,
                )
                Toast.makeText(ctx, "Language Board Updated", Toast.LENGTH_SHORT).show()
                languageTagList.filter { !it.isImplemented }
                    .joinToString(",\n- ").let {
                        if (it.isNotEmpty()) {
                            isWarningAlertShown = true
                            notImplementedLanguagesStringList = it
                        }
                    }
            }
        )
        NavHost(
            navController = navController,
            startDestination = StartScreen,
        ) {
            composable<StartScreen> { StartScreen.Show() }
        }
        if (isWarningAlertShown) {
            AlertDialog(
                onDismissRequest = {
                    notImplementedLanguagesStringList = ""
                    isWarningAlertShown = false
               },
                title = { Text("Warning") },
                text = { Text("The following languages are not yet implemented:\n$notImplementedLanguagesStringList.") },
                confirmButton = {
                    notImplementedLanguagesStringList = ""
                    isWarningAlertShown = false
                },
            )
        }
    }
}

