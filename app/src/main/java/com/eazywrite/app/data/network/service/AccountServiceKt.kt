package com.eazywrite.app.data.network.service

import com.eazywrite.app.data.model.CommonResponse
import retrofit2.http.DELETE

interface AccountServiceKt {
    @DELETE("account/logout")
    suspend fun logout(): CommonResponse
}