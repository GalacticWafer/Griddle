package com.galacticware.griddle.domain.model.emojis

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emojis")
data class Emoji(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val codepoints: String,
    val description: String
)