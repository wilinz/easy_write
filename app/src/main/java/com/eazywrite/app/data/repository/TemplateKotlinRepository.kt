package com.eazywrite.app.data.repository

import com.eazywrite.app.data.network.Network

//这是一个kotlin Repository模板示例
object TemplateKotlinRepository {

    var cache: Any? = null

    suspend fun getAny(): Any {
        if (cache == null) {
            cache = Network.templateKotlinService.getAny()
        }
        return cache!!;
    }
}