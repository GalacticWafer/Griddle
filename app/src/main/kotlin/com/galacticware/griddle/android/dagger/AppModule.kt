package com.galacticware.griddle.android.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.galacticware.griddle.domain.model.error.Errors
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.keyboard.GriddleEnglishKeyBoardBuilder
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementDao
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementDatabase
import com.galacticware.griddle.domain.model.textreplacement.TextReplacementViewModel
import com.galacticware.griddle.domain.view.KeyboardView
import java.time.LocalDateTime
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface KeyboardComponent {
    fun inject(target: KeyboardView)
    fun inject(viewModel: TextReplacementViewModel)
}

interface KeyboardFactory {
    fun createKeyboard(context: Context): Keyboard
}

@Module
@InstallIn(SingletonComponent::class)
class AppModule(private val context: Context) {
    // empty constructor for hilt
    constructor() : this(Application())

    @Provides
    fun provideKeyboardFactory(): KeyboardFactory {
        if (LocalDateTime.now().isAfter(LocalDateTime.of(2025, 5, 1, 0, 0))) {
            throw Errors.EXPIRED_TEST_APP.send()
        }
        return object : KeyboardFactory {
            override fun createKeyboard(context: Context): Keyboard {
                return Keyboard.loadKeyboard(context)?: createDefaultKeyboard(context)
            }

            private fun createDefaultKeyboard(context: Context): Keyboard {
                return GriddleEnglishKeyBoardBuilder.build(context)
            }
        }
    }

    @Provides @Singleton fun provideContext(application: Application) = application.applicationContext

    @Provides fun provideKeyboard(): Keyboard {
//        val griddleEnglishLayer = GriddleEnglishLayer(context)
        // We have to manually create the first layer to prevent the Keyboard.currentLayer
        // (which is a lateinit var) from crashing the app.
        return Keyboard.loadKeyboard(context)?: GriddleEnglishKeyBoardBuilder.build(context)
    }

    @Provides @Singleton fun provideTextReplacementDatabase(context: Application): TextReplacementDatabase {
        return Room.databaseBuilder(context, TextReplacementDatabase::class.java, "text_replacement_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideTextReplacementScreenDao(database: TextReplacementDatabase): TextReplacementDao {
        return database.textReplacementScreenDao
    }
}
