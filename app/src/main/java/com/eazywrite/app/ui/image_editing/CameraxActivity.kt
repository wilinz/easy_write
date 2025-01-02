@file:OptIn(
    ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class
)

package com.eazywrite.app.ui.image_editing

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.util.copyToCacheFile
import com.eazywrite.app.util.screenHeight
import com.eazywrite.app.util.screenWidth
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * @author wilinz
 * @date 2023/3/2 8:34
 */
class CameraXActivity : ComponentActivity() {

    private var isFlashOpen: Boolean by mutableStateOf(false)

    private val viewModel by viewModels<CameraxViewModel>()

    private val images get() = viewModel.images

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                lifecycleScope.launch {
                    val files = uris.map { it.copyToCacheFile(this@CameraXActivity) }.reversed()
                    images.addAll(0, files)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {

            EazyWriteTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var previewView by remember {
                            mutableStateOf<PreviewView?>(null)
                        }
                        val scope = rememberCoroutineScope()
                        val cameraPermissionState = rememberPermissionState(
                            Manifest.permission.CAMERA,
                            onPermissionResult = { ok ->
                                if (ok) {
                                    scope.launch {
                                        while (previewView == null) {
                                            delay(1)
                                        }
                                        startCamera(
                                            previewView!!,
                                            Size(screenWidth, screenHeight)
                                        )
                                    }
                                }
                            }
                        )

                        AndroidView(
                            factory = {
                                PreviewView(it).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                    )
                                    previewView = this
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            update = {
                            }
                        )

                        var size by remember {
                            mutableStateOf(androidx.compose.ui.geometry.Size.Zero)
                        }
                        var focusOffset by remember {
                            mutableStateOf(Offset.Zero)
                        }

                        Scaffold(
                            modifier = Modifier
                                .onSizeChanged {
                                    size = it.toSize()
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = { offset ->
                                        focusOffset = offset
                                        val factory: MeteringPointFactory =
                                            SurfaceOrientedMeteringPointFactory(
                                                size.width, size.height
                                            )
                                        val autoFocusPoint = factory.createPoint(offset.x, offset.y)
                                        try {
                                            camera?.cameraControl?.startFocusAndMetering(
                                                FocusMeteringAction
                                                    .Builder(
                                                        autoFocusPoint,
                                                        FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE or FocusMeteringAction.FLAG_AWB
                                                    )
                                                    .apply {
                                                        //focus only when the user tap the preview
                                                        disableAutoCancel()
                                                    }
                                                    .build()
                                            )
                                        } catch (e: CameraInfoUnavailableException) {
                                            Log.d("ERROR", "cannot access camera", e)
                                        }
                                    })
                                }
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, _, zoom, _ ->
                                        val scale =
                                            camera?.cameraInfo?.zoomState?.value?.zoomRatio
                                        scale?.let {
                                            camera?.cameraControl?.setZoomRatio(it * zoom)
                                        }
                                    }
                                },
                            containerColor = Color.Transparent,
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(text = "智能识别票据", color = Color.White)
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            finish()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "返回",
                                                tint = Color.White,
                                            )
                                        }
                                    },
                                    actions = {


                                        var showDeleteDialog by rememberSaveable {
                                            mutableStateOf(false)
                                        }
                                        if (showDeleteDialog) {
                                            AlertDialog(onDismissRequest = {
                                                showDeleteDialog = false
                                            },
                                                title = { Text(text = "确定清空图片") },
                                                text = { Text(text = "清空图片") },
                                                confirmButton = {
                                                    TextButton(onClick = {
                                                        showDeleteDialog = false
                                                        viewModel.images.apply {
                                                            forEach {
                                                                it.delete()
                                                            }
                                                            clear()
                                                        }
                                                    }) {
                                                        Text(text = "确定")
                                                    }
                                                },
                                                dismissButton = {
                                                    TextButton(onClick = {
                                                        showDeleteDialog = false
                                                    }) {
                                                        Text(text = "取消")
                                                    }
                                                }
                                            )
                                        }
                                        IconButton(onClick = {
                                            showDeleteDialog = true
                                        }) {
                                            Icon(
                                                painterResource(id = R.drawable.delete_xml),
                                                contentDescription = "清空",
                                                tint = Color.White
                                            )
                                        }


                                        IconButton(onClick = {
                                            camera?.cameraControl?.enableTorch(!isFlashOpen)
                                        }) {
                                            Icon(
                                                imageVector = if (isFlashOpen) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                                                contentDescription = "灯光",
                                                tint = Color.White,
                                            )
                                        }
                                    }
                                )
                            },
                            bottomBar = {
                                BottomActions(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                )
                            }
                        ) { pad ->
                            Box(
                                modifier = Modifier
                                    .padding(pad)
                                    .fillMaxSize()
                            ) {

                            }
                        }

                        FocusAnimationCircle(focusOffset)

                        LaunchedEffect(key1 = Unit, block = {
                            cameraPermissionState.launchPermissionRequest()
                        })

                    }
                }
            }
        }
    }

    @Composable
    fun FocusAnimationCircle(offset: Offset) {
        var isShow by remember { mutableStateOf(false) }
        var centerOffset by remember { mutableStateOf(IntOffset.Zero) }
        var size by remember { mutableStateOf(100.dp) }
        val scope = rememberCoroutineScope()
        var lastJob by remember { mutableStateOf<Job?>(null) }
        val localDensity = LocalDensity.current
        LaunchedEffect(offset) {
            centerOffset = offset.round()
            lastJob?.cancel()
            lastJob = scope.launch {
                isShow = true
                val max = 80
                val min = 60
                val fps = 120
                val duration = 100

                val delayDuration = 1000 / fps
                size = max.dp
                val times = (duration / delayDuration.toFloat()).toInt()
                val step = (max - min).toFloat() / times
                for (i in 0 until times) {
                    size -= step.dp
                    val pxSize = with(localDensity) { size.toPx() }
                    val radius = pxSize / 2
                    centerOffset =
                        Offset(offset.x - radius, offset.y - radius).round()
                    delay(delayDuration.toLong())
                }
                isShow = false
            }
        }
        if (isShow) {
            Surface(
                color = Color.Transparent,
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White),
                modifier = Modifier
                    .offset { centerOffset }
                    .size(size)
            ) {

            }
        }
    }

    fun Modifier.fadeOutAnimation(): Modifier {
        return this.animateContentSize(animationSpec = tween(durationMillis = 1000)) { initialValue, targetValue -> }
    }

    @Composable
    private fun BottomActions(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .padding(bottom = 64.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f), Alignment.Center
                ) {
                    if (images.isEmpty()) {
                        IconButton(onClick = {
                            toast(text = "长按多选")
                            albumLauncher.launch("image/*")
                        }) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "相册",
                                tint = Color.White,
                            )
                        }
                    } else {
                        Box(
                            Modifier
                                .size(54.dp)
                        ) {
                            AsyncImage(
                                model = images.first(),
                                modifier = Modifier
                                    .padding(7.dp)
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        toast("长按多选")
                                        albumLauncher.launch("image/*")
                                    },
                                contentDescription = "图片",
                                contentScale = ContentScale.Crop
                            )
                            RedDot(Modifier.align(Alignment.TopEnd), images.size)
                        }
                    }

                }
                Box(modifier = Modifier.weight(1f), Alignment.Center) {
                    CaptureButton {
                        takePhoto()
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f), Alignment.Center
                ) {
                    val context = LocalContext.current
                    IconButton(onClick = {
                        if (images.isEmpty()) {
                            toast( "请先拍照或选择图片")
                            return@IconButton
                        }
                        ImagePreviewActivity.start(context, images)
                    }) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "完成",
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun RedDot(modifier: Modifier = Modifier, number: Int) {
        Surface(
            color = Color.Red, modifier = modifier
                .size(14.dp),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                Alignment.Center
            ) {
                val fontSize = with(LocalDensity.current) {
                    10.dp.toSp()
                }
                Text(
                    text = "$number",
                    fontSize = fontSize,
                    color = Color.White
                )
            }
        }
    }

    @Composable
    fun CaptureButton(onClick: () -> Unit) {
        Surface(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            color = Color.White,
            shadowElevation = 8.dp,
            tonalElevation = 8.dp
        ) {

            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Capture",
                tint = Color.DarkGray,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }

    private fun startFocusAndMetering(camera: Camera) {
        val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f)
            .createPoint(.5f, .5f)
        try {
            val autoFocusAction = FocusMeteringAction.Builder(
                autoFocusPoint,
                FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE or FocusMeteringAction.FLAG_AWB
            ).apply {
                //start auto-focusing after 2 seconds
                setAutoCancelDuration(2, TimeUnit.SECONDS)
            }.build()
            camera.cameraControl.startFocusAndMetering(autoFocusAction)
        } catch (e: CameraInfoUnavailableException) {
            Log.d("ERROR", "cannot access camera", e)
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
        val photoFile = File(cacheDir, "/image/${LocalDateTime.now().format(dateFormatter)}.jpg")
        photoFile.parentFile?.mkdirs()
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: return
                    images.add(0, savedUri.toFile())
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d(TAG, msg)
                }
            })
    }

    private var imageCapture: ImageCapture? = null

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_TYPE = "image/jpeg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    /**
     *  [androidx.camera.core.ImageAnalysis.Builder] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
//        val previewRatio = max(width, height).toDouble() / min(width, height)
//        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
//            return AspectRatio.RATIO_4_3
//        }
        return AspectRatio.RATIO_16_9
    }

    private var camera: Camera? = null

    private fun startCamera(previewView: PreviewView, size: Size) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        val layoutParams = previewView.layoutParams
        Log.d(TAG, "Screen metrics: ${layoutParams.width} x ${layoutParams.height}")

        val screenAspectRatio = aspectRatio(layoutParams.width, layoutParams.height)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = previewView.display.rotation

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                // We request aspect ratio but no resolution to match preview config, but letting
                // CameraX optimize for whatever specific resolution best fits our use cases
//                .setTargetAspectRatio(screenAspectRatio)
                .setTargetResolution(
                    size
                )
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

                camera!!.cameraInfo.torchState.observe(this) {
                    isFlashOpen = it == TorchState.ON
                }

                startFocusAndMetering(camera!!)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


}

inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }
}

const val TAG = "CameraxActivity"
