package com.eazywrite.app.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.database.getStringOrNull
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")

suspend fun Uri.toFileOrCopyToFile(context: Context): File = withContext(Dispatchers.IO) {
    return@withContext if (this@toFileOrCopyToFile.scheme == ContentResolver.SCHEME_CONTENT) {
        copyToCacheFile(context)
    } else {
        toFile()
    }
}

fun Uri.copyToFile(context: Context, file: File) {
    context.contentResolver.openInputStream(this).use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }
}

suspend fun Uri.copyToCacheFile(context: Context, filename: String? = null): File =
    withContext(Dispatchers.IO) {
        val name = filename ?: this@copyToCacheFile.getFileName(context) ?: LocalDateTime.now().format(dateFormatter)
        val file = File(context.cacheDir, "/uri_to_file/${name}")
        file.createFile()
        copyToFile(context, file)
        return@withContext file
    }

fun Uri.getFileName(context: Context): String? {
    var result: String? = null
    if (this.scheme == "content") {
        val cursor = context.contentResolver.query(this, null, null, null, null)
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getStringOrNull(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = this.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

fun Uri.getMimeType(context: Context): String? {
    val uri = this
    val mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        context.contentResolver.getType(uri)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
            uri.toString()
        )
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
    }
    return mimeType
}