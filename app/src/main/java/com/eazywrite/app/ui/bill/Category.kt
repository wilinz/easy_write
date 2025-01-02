@file:OptIn(ExperimentalMaterial3Api::class)

package com.eazywrite.app.ui.bill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.model.Categories
import com.eazywrite.app.data.repository.CategoryRepository

@Composable
fun CategoryPickerDialog(
    currentType: String? = null,
    currentCategory: String? = null,
    categories: Categories,
    nullCategoriesButtonText: String = "全部类别",
    onDismissRequest: () -> Unit,
    onSelected: (type: String?, category: String?) -> Unit,
    properties: DialogProperties = DialogProperties(),
) {
    var selectedCategory by remember {
        mutableStateOf<String?>(currentCategory)
    }
    var type by remember {
        mutableStateOf<String?>(currentType)
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        confirmButton = {
            TextButton(
                onClick = { onSelected(type, selectedCategory) },
                enabled = selectedCategory != null && type != null
            ) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onSelected(null, null)
            }) {
                Text(text = nullCategoriesButtonText)
            }
        },
        title = {
            Text(text = "选择类别")
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
//                    .padding(24.dp)
                    .wrapContentSize()
                    .height(300.dp)
//                    .wrapContentHeight()
            ) {
                item(span = { GridItemSpan(2) }) {
                    Box(modifier = Modifier.fillMaxWidth(), Alignment.Center) {
                        Text(
                            text = if (categories.outList.isEmpty()) "支出（暂无数据）" else "支出",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
                items(categories.outList) { item ->
                    Item(
                        category = item,
                        selected = type == Bill.TYPE_OUT && selectedCategory == item,
                        onSelected = { sct ->
                            type = Bill.TYPE_OUT
                            selectedCategory = sct
                        },
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    Box(modifier = Modifier.fillMaxWidth(), Alignment.Center) {
                        Text(
                            text = if (categories.inList.isEmpty()) "收入（暂无数据）" else "收入",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
                items(categories.inList) { item ->
                    Item(
                        category = item,
                        selected = type == Bill.TYPE_IN && selectedCategory == item,
                        onSelected = { sct ->
                            type = Bill.TYPE_IN
                            selectedCategory = sct
                        },
                    )
                }
            }

        },
    )
}

@Composable
private fun Item(selected: Boolean, category: String, onSelected: (String) -> Unit) {
    TextButton(
        onClick = {
            if (!selected) {
                onSelected(category)
            }
        },
        colors = if (selected) ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        ) else ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Text(
            text = category,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Preview()
@Composable
private fun CategoryPickerDialogPreview() {
    CategoryPickerDialog(
        categories = CategoryRepository.getCategories(),
        onDismissRequest = {},
        onSelected = { _, _ -> })
}