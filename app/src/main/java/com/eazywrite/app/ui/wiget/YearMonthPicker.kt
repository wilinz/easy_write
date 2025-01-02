package com.eazywrite.app.ui.wiget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun YearPicker(
    currentYear: Int,
    onSelected: (year: Int) -> Unit,
    onDismissRequest: (() -> Unit),
    title: @Composable (() -> Unit)? = {
        Text(
            text = "请选择年份",
            style = MaterialTheme.typography.titleSmall
        )
    }
) {

    var year by rememberSaveable(currentYear) {
        mutableStateOf(currentYear)
    }

    AlertDialog(
        title = title,
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = { year-- }) {
                        Icon(
                            modifier = Modifier
                                .rotate(90f),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }

                    TextButton(onClick = { }) {
                        Text(
                            text = year.toString(),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    IconButton(onClick = { year++ }) {
                        Icon(
                            modifier = Modifier
                                .rotate(-90f),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }


                }

                Spacer(modifier = Modifier.height(16.dp))

                YearSelection(year = year, onYearSelected = { year = it })

            }

        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(text = "取消")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSelected(
                        year
                    )
                },
            ) {
                Text(text = "确定")
            }
        },
        onDismissRequest = {
            onDismissRequest()
        }
    )


}

@Composable
fun YearMonthPicker(
    currentMonth: Int,
    currentYear: Int,
    onSelected: (year: Int, month: Int) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)? = {
        Text(
            text = "请选择年月",
            style = MaterialTheme.typography.titleSmall
        )
    }
) {


    var month by rememberSaveable(currentMonth) {
        mutableStateOf(currentMonth)
    }
    var year by rememberSaveable(currentYear) {
        mutableStateOf(currentYear)
    }

    var showYearSelection by rememberSaveable {
        mutableStateOf(false)
    }

    AlertDialog(
        title = title,
        text = {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = { year-- }) {
                        Icon(
                            modifier = Modifier
                                .rotate(90f),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }

                    TextButton(onClick = { showYearSelection = !showYearSelection }) {
                        Text(
                            text = year.toString(),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    IconButton(onClick = { year++ }) {
                        Icon(
                            modifier = Modifier
                                .rotate(-90f),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }


                }


                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = !showYearSelection,
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessHigh)),
                    exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessHigh))
                ) {
                    MonthSelection(month = month, onMonthSelected = { month = it })
                }
                AnimatedVisibility(
                    visible = showYearSelection,
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessHigh)),
                    exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessHigh))
                ) {
                    YearSelection(year = year, onYearSelected = { year = it })
                }


            }

        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(text = "取消")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (showYearSelection) {
                        showYearSelection = false
                    } else {
                        onSelected(
                            year,
                            month
                        )
                    }

                },
            ) {
                Text(text = "确定")
            }
        },
        onDismissRequest = onDismissRequest
    )


}

@Composable
private fun MonthSelection(month: Int, onMonthSelected: (month: Int) -> Unit) {
    val months = remember {
        (1..12).toList()
    }
    LazyVerticalGrid(
        state = rememberLazyGridState(),
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        items(months) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            onMonthSelected(it)
                        }
                    )
                    .background(
                        color = if (month != it) Color.Transparent else MaterialTheme.colorScheme.primary
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = it.toString(),
                    color = if (month == it) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )

            }
        }

    }
}

@Composable
private fun YearSelection(year: Int, onYearSelected: (year: Int) -> Unit) {
    val years = remember {
        (1900..2100).toList()
    }
    val index = years.indexOf(year)
    val lazyGridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = kotlin.math.max(
            index - 6,
            0
        )
    )
    Column {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            items(years) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClick = {
                                onYearSelected(it)
                            }
                        )
                        .background(
                            color = if (year != it) Color.Transparent else MaterialTheme.colorScheme.primary
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = it.toString(),
                        color = if (year == it) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge
                    )

                }
            }
        }

    }
}









