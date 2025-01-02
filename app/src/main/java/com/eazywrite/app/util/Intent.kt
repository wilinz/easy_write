package com.eazywrite.app.util

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import com.eazywrite.app.ui.image_editing.ImagePreviewActivity
import java.io.Serializable

inline fun <reified T : Parcelable?> Intent.getParcelableArrayListExtraCompat(key: String): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableArrayListExtra(ImagePreviewActivity.KEY_IMAGES_URI, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        this.getParcelableArrayListExtra(key)
    }
}


inline fun <reified T : Serializable> Intent.getSerializableArrayListExtraCompat(key: String): List<T> {
    val serializable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getSerializableExtra(ImagePreviewActivity.KEY_IMAGES_URI, ArrayList::class.java)
    } else {
        @Suppress("DEPRECATION")
        this.getSerializableExtra(key)
    } as? ArrayList<*>
    return serializable?.map { it as T } ?: arrayListOf()
}

inline fun <reified T : Parcelable?> Intent.getParcelableExtraCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableExtra(ImagePreviewActivity.KEY_IMAGES_URI, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        this.getParcelableExtra<T>(key)
    }
}