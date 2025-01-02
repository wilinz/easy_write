package com.eazywrite.app.ui.importbill

import android.app.Application
import com.eazywrite.app.data.model.ImportBillData
import com.eazywrite.app.util.substringUnicode
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WechatImportViewModel(application: Application) : ImportViewModel(application) {

    override suspend fun importImpl(
        csvFile: File
    ) = importWechat(csvFile)


    private suspend fun importWechat(csvFile: File): List<ImportBillData> {
        val bills = csvReader {
            insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.EMPTY_STRING
            excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.TRIM
        }.openAsync(csvFile) {
            val bills = mutableListOf<ImportBillData>()
            var startIndex = -1
            readAllAsSequence(11).forEachIndexed { index, row0 ->
                val row = row0.map { it.trim() }
                if (row[0].contains("微信支付账单明细列表")) {
                    startIndex = index + 2
                }
                if (startIndex != -1 && index >= startIndex) {
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val datetime = LocalDateTime.from(dateTimeFormatter.parse(row[0]))
                    val type = when (row[4]) {
                        "收入" -> "in"
                        "支出" -> "out"
                        else -> "other"
                    }
                    if (type == "other") return@forEachIndexed
                    val amount = row[5].substringUnicode(1).toBigDecimal()
                    val comment = if (row[10] == "/") "" else row[10]
                    val name = if (row[3] == "/") "未命名" else row[3]
                    bills.add(
                        ImportBillData(
                            datetime = datetime,
                            category = row[1],
                            transactionPartner = row[2],
                            name = name,
                            type = type,
                            amount = amount,
                            paymentMethod = row[6],
                            status = row[7],
                            transactionNo = "wechatpay-" + row[8],
                            shopNo = row[9],
                            comment = comment
                        )
                    )
                }
            }
            return@openAsync bills.toList()
        }
        return bills
    }

}