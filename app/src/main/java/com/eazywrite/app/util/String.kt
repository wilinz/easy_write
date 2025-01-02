package com.eazywrite.app.util

fun String.truncateString(length: Int): String {
    val codePointCount = this.codePointCount(0, this.length)
    return if (codePointCount > length) {
        this.substring(0, this.offsetByCodePoints(0, length)) + "..."
    } else {
        this
    }
}

/**
 * 返回从给定代码点索引处开始的新字符串。
 * @param startCodePoint 开始代码点的索引
 * @return 新的子字符串
 */
fun String.substringUnicode(startCodePoint: Int): String {
    val startIndex = this.offsetByCodePoints(0, startCodePoint)
    return this.substring(startIndex)
}

/**
 * 返回一个新字符串，该字符串从给定的开始代码点索引处开始，到给定的结束代码点索引处结束（不包括结束码点）。
 * @param startCodePoint 开始代码点的索引
 * @param endCodePoint 结束代码点的索引（不包括）
 * @return 新的子字符串
 */
fun String.substringUnicode(startCodePoint: Int, endCodePoint: Int): String {
    val startIndex = this.offsetByCodePoints(0, startCodePoint)
    val endIndex = this.offsetByCodePoints(startIndex, endCodePoint - startCodePoint)
    return this.substring(startIndex, endIndex)
}

fun getFileExtensionFromUrl(url: String): String? {
    val queryIndex = url.indexOf("?")
    val filenameIndex = url.lastIndexOf("/")
    val dotIndex = url.lastIndexOf(".")
    return if (dotIndex > filenameIndex && (queryIndex < 0 || dotIndex < queryIndex)) {
        url.substring(dotIndex + 1)
    } else {
        null
    }
}
