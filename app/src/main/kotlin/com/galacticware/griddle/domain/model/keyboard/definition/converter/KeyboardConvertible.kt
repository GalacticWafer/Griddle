package com.galacticware.griddle.domain.model.keyboard.definition.converter

interface KeyboardConvertible {
    fun buildLayouts(path: String)
    fun mapKeys(inputFilePath: String, outPath: String, prefix: String): String
}