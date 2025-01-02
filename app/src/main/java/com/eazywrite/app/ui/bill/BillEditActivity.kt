@file:OptIn(
    ExperimentalStdlibApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.eazywrite.app.ui.bill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.moshi
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.util.setWindow
import com.squareup.moshi.adapter

class BillEditActivity : ComponentActivity() {

    companion object {
        const val BillDataKey = "BillDataKey"
        const val BillEditActionKey = "BillEditActionKey"
        fun start(context: Context, bill: Bill, action: BillEditAction) {
            context.startActivity(
                Intent(context, BillEditActivity::class.java).putExtra(
                    BillDataKey,
                    moshi.adapter<Bill>().toJson(bill)
                ).putExtra(
                    BillEditActionKey,
                    action.name
                )
            )
        }

    }

    val model: BillEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindow(isDarkStatusBarIcon = true)

        val billData = intent?.getStringExtra(BillDataKey)?.let { json ->
            Log.d("onCreate: ", json)
            kotlin.runCatching {
                moshi.adapter<Bill>().fromJson(json)
            }.getOrNull()
        } ?: return

        val billEditAction = intent?.getStringExtra(BillEditActionKey)?.let {
            BillEditAction.valueOf(it)
        } ?: return

        val data = billData.getEditableState() ?: return
        model.initData(data, billEditAction)

        setContent {
            EazyWriteTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text(text = "编辑账单") },
                                navigationIcon = {
                                    IconButton(onClick = { this.finish() }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { model.save() }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Save,
                                            contentDescription = "保存"
                                        )
                                    }
                                }
                            )
                        },
                    ) {
                        Box(modifier = Modifier.padding(it)) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(all = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                content = {
                                    item(span = { GridItemSpan(3) }) {
                                        Column {
                                            BillEditPage(
                                                bill = model.data, modifier = Modifier
                                            )
                                            Text(
                                                text = "图片备注",
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                    itemsIndexed(model.images) { i, item ->
                                        Box(modifier = Modifier.size(128.dp)) {
                                            AsyncImage(
                                                model = item,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(128.dp)
                                                    .clip(
                                                        RoundedCornerShape(6.dp)
                                                    )
                                            )
                                            IconButton(modifier = Modifier.align(Alignment.TopEnd),
                                                onClick = { model.images.removeAt(i) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "删除图片",
                                                    tint = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    }
                                    item {
                                        val launcher = rememberLauncherForActivityResult(
                                            contract = ActivityResultContracts.GetMultipleContents(),
                                            onResult = { uriList ->
                                                model.images.addAll(uriList)
                                            }
                                        )
                                        DashedBorderWithPlus(
                                            onClick = {
                                                launcher.launch("image/*")
                                            },
                                            modifier = Modifier.size(128.dp)
                                        )
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashedBorderWithPlus(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .border(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f))
            )
            .clip(
                RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            }


    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "添加图片")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "添加图片")
        }
    }
}