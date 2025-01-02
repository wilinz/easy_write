@file:OptIn(ExperimentalMaterial3Api::class)

package com.eazywrite.app.ui.importbill

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import com.eazywrite.app.common.toast
import com.eazywrite.app.ui.wiget.CircularProgressDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Composable
fun Import(importViewModel: ImportViewModel) {
    var showPasswordDialog by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    var job = remember<Job?> { null }
    if (isLoading) {
        CircularProgressDialog(
            onDismissRequest = {
                isLoading = false
                job?.cancel()
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            onDismissRequest = {
                showPasswordDialog = false
            },
            onConfirmButton = {
                job = importViewModel.viewModelScope.launch {
                    isLoading = true
                    importViewModel.import(
                        password = it,
                        onSuccess = {
                            showPasswordDialog = false
                            toast(text = "导入成功")
                        }
                    )
                    isLoading = false
                }
            }
        )
    }
    ElevatedButton(
        onClick = {
            if (importViewModel.uri == null) {
                toast(text = "还未选择文件")
                return@ElevatedButton
            }
            job = importViewModel.viewModelScope.launch {
                isLoading = true
                importViewModel.import(null,
                    requirePassword = {
                        showPasswordDialog = true
                    },
                    onSuccess = {
                        showPasswordDialog = false
                        toast(text = "导入成功")
                    }
                )
                isLoading = false
            }
        },
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(text = "开始导入")
    }
}

@Composable
private fun PasswordDialog(
    onDismissRequest: () -> Unit,
    onConfirmButton: (String) -> Unit,
) {
    var password by rememberSaveable {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "请输入压缩文件密码",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(text = "请输入密码")
                    },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmButton(password)
            }) {
                Text(
                    text = "确定",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(
                    text = "取消",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false)
    )
}

@Composable
fun FileInput(onFileInput: (Uri) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            Log.d("TAG", "FileInput: $it")
            if (it != null) {
                onFileInput(it)
            }
        }
    )

    Row {
        ElevatedButton(
            onClick = {
                launcher.launch(getDocumentMime())
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(text = "选择本地文件（不推荐）")
        }

        var isShowDialog by remember {
            mutableStateOf(false)
        }
//        TextButton(onClick = { isShowDialog = true }) {
//            Icon(Icons.Default.HelpOutline, contentDescription = null)
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(text = "如何选择微信QQ的文件？")
//        }

        if (isShowDialog) {
            AlertDialog(
                title = { Text(text = "如何选择微信QQ的文件？") },
                text = {
                    Text(text = "微信或QQ打开要导入的文件->右上角->其他应用->导入微信账单")
                },
                onDismissRequest = { isShowDialog = false },
                confirmButton = {
                    TextButton(onClick = { isShowDialog = false }) {
                        Text(text = "确定")
                    }
                }
            )
        }

    }

}

fun getDocumentMime() = arrayOf(
    MimeType.Zip,
    MimeType.Csv,
    MimeType.Csv2,
)