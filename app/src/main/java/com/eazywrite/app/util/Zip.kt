package com.eazywrite.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import java.io.File

suspend fun File.isEncryptedZip(): Boolean = withContext(Dispatchers.IO) {
    ZipFile(this@isEncryptedZip).isEncrypted
}

suspend fun unzipWithPassword(file: File, destinationDir: String, password: String = "") =
    withContext(Dispatchers.IO) {
        val zipFile = ZipFile(file)
        if (zipFile.isEncrypted) zipFile.setPassword(password.toCharArray())
        zipFile.extractAll(destinationDir)
    }