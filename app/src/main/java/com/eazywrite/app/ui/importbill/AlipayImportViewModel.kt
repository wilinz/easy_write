package com.eazywrite.app.ui.importbill

import android.app.Application
import com.eazywrite.app.data.model.ImportBillData
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlipayImportViewModel(application: Application) : ImportViewModel(application) {

    override suspend fun importImpl(
        csvFile: File,
    ) = importAlipay(csvFile)


    private suspend fun importAlipay(csvFile: File): List<ImportBillData> {

        val bills = csvReader {
            insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.EMPTY_STRING
            excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.TRIM
            charset = "GBK"
        }.openAsync(csvFile) {
            val bills = mutableListOf<ImportBillData>()
            var startIndex = -1
            readAllAsSequence(13).forEachIndexed { index, row0 ->
                val row = row0.map { it.trim() }
                if (row[0].contains("电子客户回单")) {
                    startIndex = index + 2
                }
                if (startIndex != -1 && index >= startIndex) {
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val datetime = LocalDateTime.from(dateTimeFormatter.parse(row[0]))
                    val type = when (row[5]) {
                        "收入" -> "in"
                        "支出" -> "out"
                        else -> "other"
                    }
                    if (type == "other") return@forEachIndexed
                    val amount = row[6].toBigDecimal()
                    val comment = if (row[11] == "/") "" else row[11]
                    val name = if (row[4] == "/") "未命名" else row[4]
                    bills.add(
                        ImportBillData(
                            datetime = datetime,
                            category = row[1],
                            transactionPartner = row[2],
                            partnerAccount = row[3],
                            name = name,
                            type = type,
                            amount = amount,
                            paymentMethod = row[7],
                            status = row[8],
                            transactionNo = "alipay-" + row[9],
                            shopNo = row[10],
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