package com.eazywrite.app.util

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")

fun getRandomName(): String {
    return LocalDateTime.now().format(dateFormatter)
}

fun createFile(path: String): File {
    val file = File(path)
    file.parentFile?.let {
        if (!it.exists()) {
            it.mkdirs()
        }
    }
    file.createNewFile()
    return file
}

fun File.createFile(): File {
    val file = File(path)
    file.parentFile?.let {
        if (!it.exists()) {
            it.mkdirs()
        }
    }
    file.createNewFile()
    return file
}

fun getFileExtension(string: String): String {
    return File(string).extension
}

fun File.newFileAddNumberSuffix(): File {
    var i = 0
    while (true) {
        val newFileName = "$nameWithoutExtension(${i + 2}).$extension"
        val newFile = File(this.parent!!, newFileName)
        if (!newFile.exists()) {
            newFile.createFile()
            return newFile
        }
        i++
    }
}

fun File.deleteChildren() {
    if (isDirectory) {
        listFiles()?.forEach {
            if (it.isDirectory) {
                it.deleteRecursively()
            } else {
                it.delete()
            }
        }
    }
}