package com.eazywrite.app.data.repository

import com.eazywrite.app.data.database.db
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.model.Categories

object CategoryRepository {

    fun getCategories(): Categories {
        return Categories(
            inList = getInList(),
            outList = getOutList()
        )
    }

    suspend fun getAllCategories(): Categories {
        val inList = db.billDao().getAllCategories(Bill.TYPE_IN).toMutableList()
            .apply { addAll(getInList()) }.distinct()
        val outList = db.billDao().getAllCategories(Bill.TYPE_OUT).toMutableList()
            .apply { addAll(getOutList()) }.distinct()
        return Categories(
            inList = inList,
            outList = outList
        )
    }

    suspend fun getAllDBCategories(): Categories {
        val inList = db.billDao().getAllCategories(Bill.TYPE_IN)
        val outList = db.billDao().getAllCategories(Bill.TYPE_OUT)
        return Categories(
            inList = inList,
            outList = outList
        )
    }

    fun getOutList(): List<String> {
        return listOf(
            "餐饮",
            "交通",
            "服饰",
            "购物",
            "服务",
            "教育",
            "娱乐",
            "运动",
            "生活缴费",
            "旅行",
            "宠物",
            "医疗",
            "保险",
            "公益",
            "发红包",
            "转账",
            "人情",
            "其他"
        )
    }

    fun getInList(): List<String> {
        return listOf(
            "生意",
            "工资",
            "资金",
            "人情",
            "收红包",
            "收转账",
            "退款",
            "其他",
        )
    }

}