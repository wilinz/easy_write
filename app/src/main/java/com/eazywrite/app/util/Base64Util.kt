package com.eazywrite.app.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.core.net.toFile
import com.anggrayudi.storage.extension.openInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun base64ToFile(file: File, base64: String?): File = withContext(Dispatchers.IO) {
    file.createFile()
    val bytes = Base64.decode(base64, Base64.DEFAULT)
    file.writeBytes(bytes)
    return@withContext file
}

suspend fun bitmapToFile(file: File, bitmap: Bitmap): File = withContext(Dispatchers.IO) {
    file.createFile()
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    return@withContext file
}

fun ByteArray.toBase64(): String {
    return android.util.Base64.encode(this, android.util.Base64.DEFAULT)
        .toString(Charsets.UTF_8)
}

suspend fun ByteArray.toBase64DataUrl(mimeType: String): String = withContext(Dispatchers.IO) {
    val file = this@toBase64DataUrl
    val base64Data = Base64.encodeToString(file, Base64.NO_WRAP)
    "data:$mimeType;base64,$base64Data"
}

suspend fun File.toBase64DataUrl(mimeType: String): String = withContext(Dispatchers.IO) {
    val file = this@toBase64DataUrl
    val base64Data = Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    "data:$mimeType;base64,$base64Data"
}

suspend fun Uri.toBase64DataUrl(context: Context, mimeType: String): String =
    withContext(Dispatchers.IO) {
        val uri = this@toBase64DataUrl
        val bytes = (if (uri.scheme == "content") {
            uri.openInputStream(context)?.readBytes()
        } else {
            uri.toFile().readBytes()
        }) ?: throw Exception("uri $uri body is null")
        val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
        "data:$mimeType;base64,$base64Data"
    }