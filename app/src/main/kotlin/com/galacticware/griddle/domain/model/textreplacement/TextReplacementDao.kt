package com.galacticware.griddle.domain.model.textreplacement

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TextReplacementDao {
    @Upsert
    suspend fun upsertTextReplacement(textReplacement: TextReplacement)

    @Delete
    suspend fun deleteTextReplacement(textReplacement: TextReplacement)

    @Query("SELECT * FROM textReplacements")
    fun getAllTextReplacements(): Flow<List<TextReplacement>>

    @Query("SELECT * FROM textReplacements ORDER BY abbreviation")
    fun getTextReplacementsByAbbreviation(): Flow<List<TextReplacement>>

    @Query("SELECT * FROM textReplacements order by replacement")
    fun getTextReplacementsByReplacement(): Flow<List<TextReplacement>>

    @Query("SELECT * FROM textReplacements WHERE requiresWhitespaceBefore != 1")
    fun getAllTextReplacementsByWhiteSpaceRequirement(): Flow<List<TextReplacement>>
}

