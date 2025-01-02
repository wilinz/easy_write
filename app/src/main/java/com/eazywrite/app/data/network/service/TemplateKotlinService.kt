package com.eazywrite.app.data.network.service

import android.database.Observable

//这是一个kotlin api 接口示例
interface TemplateKotlinService {
    //    用suspend，直接返回对象
    suspend fun getAny(): Any
}