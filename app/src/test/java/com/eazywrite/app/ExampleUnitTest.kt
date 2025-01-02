package com.eazywrite.app

import com.eazywrite.app.util.extractJsonFromString
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        println(extractJsonFromString("""帮我解析“今天买电脑花了8000块钱”变成下面的json格式，今天是 2023 年 3月18日，没有的信息用空字符串表示
{
    "amount": 1000,
    "comment": "",
    "date": "yyyy-MM-dd HH:mm:ss",
    "category": "消费类别",
    "shop": "商店",
    "name": "商品名称"
}
"""))
    }
}