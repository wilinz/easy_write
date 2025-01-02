package com.eazywrite.app.ui.image_editing

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import java.io.File

/**
 * @author wilinz
 * @date 2023/3/2 8:34
 */
class CameraxViewModel(application: Application) : AndroidViewModel(application) {

    val images = mutableStateListOf<File>()

    override fun onCleared() {
        super.onCleared()
        images.forEach {
            it.delete()
        }
    }
}