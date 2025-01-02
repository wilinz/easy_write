package com.eazywrite.app.data.repository

import com.eazywrite.app.data.network.Network

object AppVersionRepository {

    suspend fun getAppVersion() = Network.appVersionService.getAppVersion()

}