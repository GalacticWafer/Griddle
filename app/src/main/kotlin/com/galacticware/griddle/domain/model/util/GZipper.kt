package com.galacticware.griddle.domain.model.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GZipper {
    fun gzip(data: ByteArray): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { gzipStream ->
            gzipStream.write(data)
        }
        return outputStream.toByteArray()
    }

    fun ungzip(data: ByteArray): ByteArray {
        val inputStream = ByteArrayInputStream(data)
        GZIPInputStream(inputStream).use { gzipStream ->
            val outputStream = ByteArrayOutputStream()
            gzipStream.copyTo(outputStream)
            return outputStream.toByteArray()
        }
    }
}