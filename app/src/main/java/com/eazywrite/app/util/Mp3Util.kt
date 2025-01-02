package com.eazywrite.app.util

import android.media.MediaMetadataRetriever

fun getMp3FileDuration(filePath: String): Long {
    val durationStr = runCatching {
        MediaMetadataRetriever().apply { setDataSource(filePath) }
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    }.getOrNull()
    return durationStr?.toLong() ?: 0L
}