package com.eazywrite.app.ui.bill

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.StringList
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.util.toBase64DataUrl
import kotlinx.coroutines.launch

class BillEditViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var data: BillEditableState
    lateinit var billEditAction: BillEditAction
    var images = mutableStateListOf<Uri>()
    fun initData(data: BillEditableState, billEditAction: BillEditAction) {
        this.billEditAction = billEditAction
        this.data = data
        data.imagesComment?.let { comment ->
            this.images.addAll(comment.map { Uri.parse(it) })
        }
    }

    fun save() {
        viewModelScope.launch {
            data.imagesComment = StringList.of(images.map {
                if (it.scheme != "http" && it.scheme != "https") {
                    kotlin.runCatching { it.toBase64DataUrl(getApplication(), "image/jpeg") }
                        .getOrNull()
                } else {
                    it.toString()
                }
            }.filterNotNull())
            kotlin.runCatching {
                if (billEditAction == BillEditAction.ADD) {
                    BillRepository.addBill(data.toBill()).also {
                        Log.d("save: ", it.toString())
                    }
                } else {
                    BillRepository.update(data.toBill()).also {
                        Log.d("save: ", it.toString())
                    }
                }
            }.onSuccess {
                toast("保存成功")
            }.onFailure {
                it.printStackTrace()
                toast("保存失败")
            }
        }
    }

}