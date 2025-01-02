package com.eazywrite.app.ui.importbill

import java.io.File

/**
 * @author wilinz
 * @date 2023/4/11 17:34
 */
fun searchCSVFile(directory: File): File? {
    if (!directory.isDirectory) {
        return null
    }
    for (file in directory.listFiles() ?: emptyArray()) {
        if (file.isDirectory) {
            val csvFile = searchCSVFile(file)
            if (csvFile != null) {
                return csvFile
            }
        } else if (file.name.endsWith(".csv", true)) {
            return file
        }
    }
    return null
}
