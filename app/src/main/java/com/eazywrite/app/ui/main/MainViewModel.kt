package com.eazywrite.app.ui.main

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.AppVersionData
import com.eazywrite.app.data.model.User
import com.eazywrite.app.data.repository.AppVersionRepository
import com.eazywrite.app.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    fun getCurrentUserFlow(): Flow<User?> {
        return UserRepository.getCurrentUserFlow()
    }

    var isShowUpdateDialog by mutableStateOf(false)

    init {
        viewModelScope.launch {
            kotlin.runCatching {
                val data = getAppVersion().data
                appVersion.value = data
                isShowUpdateDialog = true
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            kotlin.runCatching {
                UserRepository.logout()
            }.onSuccess {
                toast(text = "退出登录成功")
            }.onFailure {
                toast(text = "退出登录失败：${it.message}")
            }
        }
    }

    var appVersion = MutableStateFlow<AppVersionData?>(null)

    init {
//        viewModelScope.launch {
//            kotlin.runCatching {
//                appVersion.value = getAppVersion().data
//            }.onFailure {
//                it.printStackTrace()
//            }
//        }
    }

    suspend fun getAppVersion() = AppVersionRepository.getAppVersion()

}