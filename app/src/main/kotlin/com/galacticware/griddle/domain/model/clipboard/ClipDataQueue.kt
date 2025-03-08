package com.galacticware.griddle.domain.model.clipboard

import android.Manifest
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.galacticware.griddle.domain.view.composable.nestedappscreen.ClipboardScreen
import java.io.File


class ClipDataQueue(private val capacity: Int = 300) {
    val size: Int get() = capacity
    private val queue = mutableListOf<GriddleClipboardItem>()

    // Push to the second position in the list so that the main
    // clipboard item is still at the top.
    fun push(newItem: GriddleClipboardItem) {
        if(newItem.itemCount == 0) {
            return
        }
        if(newItem.getItemAt(0).text.let {
            it.isNullOrEmpty()
        } && newItem.getItemAt(0).uri.let {
            it == null || it.toString().isEmpty()
        } && newItem.getItemAt(0).htmlText.isNullOrEmpty()) {
            return
        }

        val duplicateIndex = queue.indexOfFirst {
            it.dataEquals(newItem.data)
        }

        if(duplicateIndex != -1) {
            queue.removeAt(duplicateIndex)
        } else if (queue.isNotEmpty() && queue.size > capacity) {
            queue.removeAt(capacity - 1)
        }

        val index = if ((ClipboardScreen.wasLastOperationSelectAll || duplicateIndex > -1) && queue.isNotEmpty())
            1
        else
            0

        queue.add(index, newItem)
        ClipboardScreen.wasLastOperationSelectAll = false
    }

    fun iterator(): Iterator<GriddleClipboardItem> {
        return queue.toList().iterator()
    }

    val items get() = queue.mapNotNull {
        if (it.itemCount > 0) it else null
    }
}

class GriddleClipboardItem(context: Context) {
    private val maybeData: ClipData = run {
        selectedTextData?.let{
            val temp = selectedTextData
            selectedTextData = null
            temp
        }?: (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip!!
    }

    companion object {
        var selectedTextData: ClipData? = null
        fun pushSelectedTextData(context: Context, text: String) {
            selectedTextData = ClipData.newPlainText("text", text)
            ClipboardScreen.clipDataQueue.push(GriddleClipboardItem(context))
        }
    }

    var isImage: Boolean = false
    private var imageBitmap: ImageBitmap? = null
    val data: ClipData = maybeData.let {
        val item = maybeData.getItemAt(0)
        if(item.uri != null) {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(item.uri!!)

            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageBitmap = bitmap.asImageBitmap()
            contentResolver.openOutputStream(item.uri).use { outputStream ->
                if (outputStream != null) {
                    (bitmap).compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                // Save the bitmap to your app's local data
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "pasted_image.jpg")
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "images")
                val imageUri = saveBitmapToInternalStorage(bitmap, context)
                isImage = true
                inputStream!!.close()
                inputStream.close()
                ClipData.newUri(contentResolver, "Image", imageUri)
            }
        } else maybeData
    }

    private val describeContents = data.describeContents()
    val itemCount = data.itemCount
    fun getItemAt(index: Int): ClipData.Item = data.getItemAt(index)
    val description: ClipDescription = data.description
    fun dataEquals(other: ClipData?): Boolean {
        if (other == null) return false
        val doLabelsMatch = other.description.label == description.label
        val doesFirstItemMatch = other.itemCount > 1 && itemCount > 1 && other.getItemAt(0).text == getItemAt(0).text
        val isDataPartiallyEqual = doLabelsMatch && doesFirstItemMatch
        val doDescribedContentsMatch = other.describeContents() == describeContents
        return doDescribedContentsMatch && isDataPartiallyEqual
    }
    val text = data.getItemAt(0).text?.toString()
    override fun toString(): String {
        return text?: "$data"
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap, context: Context): Uri {
        val filename = "pasted_image_${System.currentTimeMillis()}.jpg"
        val fos = context.openFileOutput(filename, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return FileProvider.getUriForFile(context, "com.galacticware.griddle.provider", File(context.filesDir, filename))
    }
}

fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
    if (uri == null) return null
    val contentResolver = context.contentResolver

    val isPermitted = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { // Check for Android 10 and below
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES

        ) == PackageManager.PERMISSION_GRANTED
    }

    if(!isPermitted) {
        return null
    }
    val cursor = contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
    return if (cursor != null && cursor.moveToFirst()) {
        val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        if(columnIndex == -1) return null
        val imagePath = cursor.getString(columnIndex)
        cursor.close()
        val imageUri = Uri.parse(imagePath)
        // Use imageUri as needed
        try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else null
}