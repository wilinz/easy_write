package com.eazywrite.app.data.model

import android.graphics.Rect
import com.google.mlkit.vision.text.Text
import kotlin.math.abs

data class GoogleMLKitOcrResult(
    val level: Int,
    val confidence: Float = -1f,
    var text: String,
    val language: String? = null,
    val bounds: Rect? = null,
    var children: List<GoogleMLKitOcrResult>? = null
) : Comparable<GoogleMLKitOcrResult> {

    fun find(predicate: (GoogleMLKitOcrResult) -> Boolean): GoogleMLKitOcrResult? {
        return recursiveFind(this, predicate)
    }

    fun find(level: Int, predicate: (GoogleMLKitOcrResult) -> Boolean): GoogleMLKitOcrResult? {
        return find { it.level == level && predicate(it) }
    }

    fun filter(predicate: (GoogleMLKitOcrResult) -> Boolean): List<GoogleMLKitOcrResult> {
        val filterList = mutableListOf<GoogleMLKitOcrResult>()
        recursiveFilter(filterList, this, false, predicate)
        return filterList.toList()
    }

    fun filter(
        level: Int,
        predicate: (GoogleMLKitOcrResult) -> Boolean
    ): List<GoogleMLKitOcrResult> {
        return filter { it.level == level && predicate(it) }
    }

    fun toArray(level: Int): List<GoogleMLKitOcrResult> {
        val filterList = mutableListOf<GoogleMLKitOcrResult>()
        recursiveFilter(filterList, this, true) { it.level == level }
        return filterList.toList()
    }

    fun toArray(): List<GoogleMLKitOcrResult> {
        val filterList = mutableListOf<GoogleMLKitOcrResult>()
        recursiveFilter(filterList, this, true) { true }
        return filterList.toList()
    }

    fun sort() {
        if (!children.isNullOrEmpty()) {
            recursiveSort(this)
        }
    }

    fun sorted(): GoogleMLKitOcrResult {
        val newResult = this.copy()
        if (!children.isNullOrEmpty()) {
            recursiveSort(newResult)
        }
        return newResult
    }

    fun formatText(){
        if (!children.isNullOrEmpty()) {
            formatText(this)
        }
    }

    companion object {

        fun formatText(mlKitOcrResult: GoogleMLKitOcrResult) {
            if (!mlKitOcrResult.children.isNullOrEmpty()) {

                val stringBuilder = StringBuilder()
                mlKitOcrResult.children!!.forEachIndexed { index, child ->
                    mlKitOcrResult.children!!.getOrNull(index - 1)?.let { lastElement ->
                        if (child.bounds!!.top >= lastElement.bounds!!.bottom - lastElement.bounds.height() / 4f) {
                            stringBuilder.append("\n")
                        }
                    }

                    when (mlKitOcrResult.level) {
                        0, 1 -> stringBuilder.append(child.text)
                        2 -> {
                            val end =
                                if (mlKitOcrResult.children!!.lastIndex == index) "\n" else " "
                            stringBuilder.append(child.text + end)
                        }
                        3 -> stringBuilder.append(child.text)
                    }
                }
                mlKitOcrResult.text = stringBuilder.toString()

                for (child in mlKitOcrResult.children!!) {
                    formatText(child)
                }
            }
        }

        private fun recursiveFind(
            mlKitOcrResult: GoogleMLKitOcrResult,
            predicate: (GoogleMLKitOcrResult) -> Boolean
        ): GoogleMLKitOcrResult? {
            if (predicate(mlKitOcrResult) && mlKitOcrResult.level > 0) return mlKitOcrResult
            if (!mlKitOcrResult.children.isNullOrEmpty()) {
                for (child in mlKitOcrResult.children!!) {
                    recursiveFind(child, predicate)?.let { return it }
                }
            }
            return null
        }

        private fun recursiveFilter(
            filterList: MutableList<GoogleMLKitOcrResult>,
            mlKitOcrResult: GoogleMLKitOcrResult,
            childToNull: Boolean = false,
            predicate: (GoogleMLKitOcrResult) -> Boolean
        ) {
            if (predicate(mlKitOcrResult) && mlKitOcrResult.level > 0) {
                filterList.add(if (childToNull) mlKitOcrResult.copy(children = null) else mlKitOcrResult)
            }
            if (!mlKitOcrResult.children.isNullOrEmpty()) {
                for (child in mlKitOcrResult.children!!) {
                    recursiveFilter(filterList, child, childToNull, predicate)
                }
            }
            return
        }

        private fun recursiveSort(mlKitOcrResult: GoogleMLKitOcrResult) {
            if (!mlKitOcrResult.children.isNullOrEmpty()) {
                mlKitOcrResult.children = mlKitOcrResult.children!!.sorted()

                val stringBuilder = StringBuilder()
                mlKitOcrResult.children!!.forEachIndexed { index, child ->
                    mlKitOcrResult.children!!.getOrNull(index - 1)?.let { lastElement ->
                        if (child.bounds!!.top >= lastElement.bounds!!.bottom - lastElement.bounds.height() / 4f) {
                            stringBuilder.append("\n")
                        }
                    }
                    stringBuilder.append(child.text)
                }
                mlKitOcrResult.text = stringBuilder.toString()

                for (child in mlKitOcrResult.children!!) {
                    recursiveSort(child)
                }
            }
        }

    }

    override fun compareTo(other: GoogleMLKitOcrResult): Int {
        val deviation = (bounds!!.height() / 2f).coerceAtLeast(other.bounds!!.height() / 2f)
        return if (abs((bounds.top + bounds.bottom) / 2f - (other.bounds.top + other.bounds.bottom) / 2f) < deviation) {
            bounds.left - other.bounds.left
        } else {
            bounds.bottom - other.bounds.bottom
        }
    }

}

fun Text.mapToOcrResults(): GoogleMLKitOcrResult {
    val result = this
    val ocrResult = GoogleMLKitOcrResult(
        level = 0,
        text = result.text,
        children = result.textBlocks.map { block ->
            GoogleMLKitOcrResult(
                level = 1,
                text = block.text,
                language = block.recognizedLanguage,
                bounds = block.boundingBox,
                children = block.lines.map { line ->
                    GoogleMLKitOcrResult(
                        level = 2,
                        confidence = line.confidence,
                        text = line.text,
                        language = line.recognizedLanguage,
                        bounds = line.boundingBox,
                        children = line.elements.map { e ->
                            GoogleMLKitOcrResult(
                                level = 3,
                                confidence = e.confidence,
                                text = e.text,
                                language = e.recognizedLanguage,
                                bounds = e.boundingBox,
                                children = null
                            )
                        }
                    )
                }
            )
        }
    )
    return ocrResult
}
