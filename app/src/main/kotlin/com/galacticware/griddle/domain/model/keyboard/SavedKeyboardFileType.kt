package com.galacticware.griddle.domain.model.keyboard

import android.content.Context
import android.util.Log
import com.galacticware.griddle.R
import com.galacticware.griddle.domain.model.util.GZipper
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.util.zip.ZipException

/**
 * Enumeration of all supported keyboard file types, and implementations for their save/load
 * strategies.
 */
enum class SavedKeyboardFileType(val fileExtension: String) {
    JSON("json"),
    ZIP("zip"),
    ;

    fun save(
        context: Context,
        jsonString: String,
        name: String
    ) {
        val fileName = fileNameForFileType(name)
        val bytes = bytesForCompressionChoice(jsonString)
        writeToFile(context, fileName, bytes)
    }

    private fun bytesForCompressionChoice(jsonString: String): ByteArray =
        when(this) {
            JSON -> jsonString.toByteArray()
            ZIP -> GZipper.gzip(jsonString.toByteArray())
        }

    private fun fileNameForFileType(name: String): String = "$name.$fileExtension"

    private fun writeToFile(
        context: Context,
        fileName: String,
        compressedBytes: ByteArray,
    ) {
        val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fileOutputStream.write(compressedBytes)
        fileOutputStream.close()
        writeFileNameTopPreferences(context, fileName)
        Log.d("KeyboardSave", "${compressedBytes.size} bytes saved to file: $fileName")
    }

    private fun writeFileNameTopPreferences(context: Context, fileName: String) {
        val key = context.getString(R.string.user_keyboard)
        val sharedPreferences =
            context.getSharedPreferences(R.string.keyboard_prefs.toString(), Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, fileName).apply()
    }

    companion object {
        fun loadKeyboard(context: Context, fileName: String): Keyboard {
            val file = File(context.filesDir, fileName)
            val bytes = file.readBytes()
            val jsonString = try {
                GZipper.ungzip(bytes).toString(Charset.defaultCharset())
            } catch (e: ZipException) {
                String(bytes)
            }
            val keyboardModel = Json.decodeFromString<KeyboardModel>(jsonString)
            val keyboardName = fileName.substringBefore(".")
            return keyboardModel.toKeyboard(context, keyboardName)
        }
    }
}