package com.galacticware.griddle.domain.model.textreplacement

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TextReplacement::class,
     ],
    version = 5,
    exportSchema = false
)
abstract class TextReplacementDatabase : RoomDatabase() {
    abstract val textReplacementScreenDao: TextReplacementDao
}