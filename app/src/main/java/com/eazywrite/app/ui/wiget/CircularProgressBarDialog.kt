@file:OptIn(ExperimentalMaterial3Api::class)

package com.eazywrite.app.ui.wiget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun CircularProgressDialog(onDismissRequest: () -> Unit) {
    var duration by rememberSaveable {
        mutableStateOf(0)
    }
    LaunchedEffect(key1 = Unit, block = {
        while (true) {
            delay(1000)
            duration++
        }
    })
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "请稍后",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${duration}s 正在处理...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "取消", color = MaterialTheme.colorScheme.primary)
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false)
    )
}

@Preview()
@Composable
fun CircularProgressBarDialogPreview() {
    CircularProgressDialog {

    }
}