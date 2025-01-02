@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.eazywrite.app.ui.audiorecord

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.repository.NotLoggedInException
import com.eazywrite.app.ui.bill.BillEditAction
import com.eazywrite.app.ui.bill.BillEditDialog
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.ui.wiget.CircularProgressDialog
import com.eazywrite.app.util.getMp3FileDuration
import com.eazywrite.app.util.setWindow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class AudioRecordActivity : AppCompatActivity() {

    private var recorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindow(isDarkStatusBarIcon = true)

        setContent {
            EazyWriteTheme() {
                Surface(color = MaterialTheme.colorScheme.background) {

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

                    val vm: AudioRecordViewModel = viewModel()

                    val context = LocalContext.current
                    val audioRecordPermission =
                        rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO) { ok ->
                            if (ok) {
                                startRecording(context, vm.audioFile)
                            } else {
                                toast("请允许录音权限")
                            }
                        }

                    val scope = rememberCoroutineScope()
                    var showDialog by rememberSaveable {
                        mutableStateOf(false)
                    }
                    if (showDialog && vm.billEditableState != null) {
                        BillEditDialog(billEditableState = vm.billEditableState!!,
                            billEditAction = BillEditAction.ADD,
                            onDismissRequest = { showDialog = false },
                            onConfirm = {
                                job = lifecycleScope.launch {
                                    isLoading = true
                                    kotlin.runCatching {
                                        vm.addBill(vm.billEditableState!!)
                                    }.onSuccess {
                                        toast("添加成功")
                                        showDialog = false
                                    }.onFailure { e ->
                                        if (e is NotLoggedInException) {
                                            toast("请先登录")
                                        } else {
                                            toast("添加失败: " + e.message)
                                        }
                                        e.printStackTrace()
                                    }
                                    isLoading = false
                                }
                            }
                        )
                    }
                    Scaffold(
                        topBar = {
                            TopAppBar(title = {
                                Text(text = "智能添加")
                            })
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "例如：今天中午点外卖花了20",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(value = vm.msg,
                                        onValueChange = { vm.msg = it },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium,
                                        trailingIcon = {
                                            IconButton(
                                                onClick = {
                                                    job = vm.viewModelScope.launch {
                                                        isLoading = true
                                                        kotlin.runCatching {
                                                            vm.toJson()
                                                        }.onSuccess {
                                                            showDialog = true
                                                        }.onFailure {
                                                            it.printStackTrace()
                                                            toast("连接服务器失败：${it.message}")
                                                        }
                                                        isLoading = false
                                                    }
                                                },
                                                enabled = vm.msg.isNotBlank()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Done,
                                                    contentDescription = "完成"
                                                )
                                            }
                                        },
                                        label = {
                                            Text(text = "请输入描述自动生成账单")
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                MyButton(onPressed = {
                                    audioRecordPermission.launchPermissionRequest()
                                }, onReleased = {
                                    job = vm.viewModelScope.launch {
                                        delay(100)
                                        stopRecording()
                                        if (getMp3FileDuration(vm.audioFile.path) < 1000) {
                                            toast("录音时间过短")
                                            return@launch
                                        }
                                        isLoading = true
                                        kotlin.runCatching {
                                            vm.transcriptions()
                                        }.onSuccess {
                                        }.onFailure {
                                            it.printStackTrace()
                                            toast("连接服务器失败：${it.message}")
                                        }
                                        isLoading = false
                                    }
                                })
                                Spacer(modifier = Modifier.height(84.dp))
                            }

                        }
                    }
                }
            }
        }
    }

    private fun startRecording(context: Context, file: File) {
        val mediaRecorder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context)
            else MediaRecorder()

        recorder = mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(file.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }

            start()
        }

    }

    private fun stopRecording() {
        recorder?.apply {
            kotlin.runCatching {
                stop()
                release()
            }
        }
        recorder = null
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
    }


    @Composable
    fun MyButton(onPressed: () -> Unit, onReleased: () -> Unit) {
        var isPressed by remember {
            mutableStateOf(false)
        }

        Surface(
//            onClick = {onPressed()},
            modifier = Modifier.size(84.dp),
            color = if (isPressed) Color.Gray else MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            try {
                                isPressed = true
                                onPressed()
                                awaitRelease()
                            } finally {
                                isPressed = false
                                onReleased()
                            }
                        })
                }
                .padding(8.dp),
                contentAlignment = Alignment.Center) {
                Text("语音", maxLines = 1)
            }

        }
    }


}

const val TAG = "AudioRecordActivity.kt"