package com.eazywrite.app.ui.importbill

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.model.ImportBillData
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.util.copyToCacheFile
import com.eazywrite.app.util.deleteChildren
import com.eazywrite.app.util.getFileExtensionFromUrl
import com.eazywrite.app.util.getFileName
import com.eazywrite.app.util.getMimeType
import com.eazywrite.app.util.getRandomName
import com.eazywrite.app.util.isEncryptedZip
import com.eazywrite.app.util.unzipWithPassword
import java.io.File

abstract class ImportViewModel(application: Application) : AndroidViewModel(application) {

    var uri: Uri? by mutableStateOf(null)
    var csvFile: File? by mutableStateOf(null)

    private val context get() = getApplication<Application>()

    private val zipFileDir by lazy {
        File(context.cacheDir, "bill_import/${getRandomName()}").apply {
            mkdirs()
        }
    }

    override fun onCleared() {
        super.onCleared()
        zipFileDir.deleteRecursively()
    }

    suspend fun import(
        password: String?,
        requirePassword: (() -> Unit)? = null,
        onSuccess: () -> Unit
    ) {
        fun success() {
            zipFileDir.deleteChildren()
            uri = null
            onSuccess()
        }
        kotlin.runCatching {
            val this1 = context
            val uri1 = uri ?: return
            val mimeType = uri1.getMimeType(this1)
            val name = uri1.getFileName(this1)
            val ext = name?.substringAfterLast('.', "") ?: getFileExtensionFromUrl(uri1.path ?: "")
            if (ext?.lowercase() == "zip" || mimeType == MimeType.Zip) {
                val cacheFile = uri1.copyToCacheFile(this1)
                if (cacheFile.isEncryptedZip() && password == null) {
                    requirePassword?.invoke()
                    return
                }
                zipFileDir.deleteChildren()
                unzipWithPassword(cacheFile, zipFileDir.path, password ?: "")
                val csvFile =
                    searchCSVFile(zipFileDir) ?: throw Exception("失败：找不到账单CSV文件")
                Log.d(TAG, "import: ${csvFile.path}")
                this.onSuccess(importImpl(csvFile))
                success()
            } else if (ext?.lowercase() == "csv" || mimeType == MimeType.Csv || mimeType == MimeType.Csv2) {
                val csvFile = uri1.copyToCacheFile(this1)
                Log.d(TAG, "import: ${csvFile.path}")
                this.onSuccess(importImpl(csvFile))
                success()
            } else {
                toast(text = "您选择的文件不是csv文件或者zip文件")
            }
        }.onFailure {
            it.printStackTrace()
            val msg = if (it.message == "Wrong password!") "密码错误" else it.javaClass.name
            toast(text = "导入失败：${msg}")
        }
    }
//    onSuccess()
//    uri = null
//    zipFileDir.deleteChildren()

    private suspend fun onSuccess(bills: List<ImportBillData>) {
        BillRepository.addBillList(bills.map { it.toBill() })
    }

    private fun ImportBillData.toBill(): Bill {
        return Bill(
            amount = this.amount,
            comment = this.comment,
            datetime = this.datetime,
            date = this.datetime.toLocalDate(),
            category = this.category,
            transactionPartner = this.transactionPartner,
            name = this.name,
            type = this.type,
            thirdPartyId = this.transactionNo
        )
    }

    protected abstract suspend fun importImpl(
        csvFile: File
    ): List<ImportBillData>

}

const val TAG = "ImportViewModel.kt"
