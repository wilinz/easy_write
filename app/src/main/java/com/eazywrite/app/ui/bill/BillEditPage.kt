@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.eazywrite.app.ui.bill

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eazywrite.app.R
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.model.BillEditable
import com.eazywrite.app.data.model.Categories
import com.eazywrite.app.data.model.StringList
import com.eazywrite.app.data.repository.CategoryRepository
import com.eazywrite.app.ui.wiget.FormFieldState
import com.eazywrite.app.ui.wiget.OutlinedFormField
import com.eazywrite.app.ui.wiget.ValidState
import com.eazywrite.app.ui.wiget.toValidState
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dataTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

private fun getTypeDisplayName(type: String) = if (type == Bill.TYPE_IN) "收入" else "支出"

private fun getTypeByDisplayName(name: String) = if (name == "收入") Bill.TYPE_IN else Bill.TYPE_OUT

class BillEditableState(
    val bill: Bill? = null,
    amount: FormFieldState,
    comment: FormFieldState,
    datetime: FormFieldState,
    category: FormFieldState,
    name: FormFieldState,
    transactionPartner: FormFieldState,
    type: FormFieldState,
    typeInput: FormFieldState,
    var imagesComment: StringList? = bill?.imagesComment,
) {
    var amount by mutableStateOf(amount)
    var comment by mutableStateOf(comment)
    var datetime by mutableStateOf(datetime)
    var category by mutableStateOf(category)
    var name by mutableStateOf(name)
    var transactionPartner by mutableStateOf(transactionPartner)
    var type by mutableStateOf(type)
    var typeInput by mutableStateOf(typeInput)
    fun toBill(): Bill {
        val isValid = listOf(
            amount,
            comment,
            datetime,
            category,
            name,
            transactionPartner,
            type,
            typeInput
        ).all { it.validator.invoke(it).isValid }
        if (!isValid) {
            throw Exception("输入有误，请检查输入")
        }
        return bill?.copy(
            amount = this.amount.text.toBigDecimal(),
            comment = this.comment.text,
            datetime = LocalDateTime.parse(this.datetime.text, dataTimeFormatter),
            date = LocalDateTime.parse(this.datetime.text, dataTimeFormatter).toLocalDate(),
            category = this.category.text,
            transactionPartner = this.transactionPartner.text,
            name = this.name.text,
            type = getTypeByDisplayName(this.typeInput.text),
            thirdPartyId = bill.thirdPartyId,
            imagesComment = imagesComment
        )
            ?: Bill(
                amount = this.amount.text.toBigDecimal(),
                comment = this.comment.text,
                datetime = LocalDateTime.parse(this.datetime.text, dataTimeFormatter),
                date = LocalDateTime.parse(this.datetime.text, dataTimeFormatter).toLocalDate(),
                category = this.category.text,
                transactionPartner = this.transactionPartner.text,
                name = this.name.text,
                type = getTypeByDisplayName(this.typeInput.text),
                imagesComment = imagesComment
            )
    }

}

private val isValidDateFormatRegex =
    "^(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})$".toRegex()

private val isValidAmountFormatRegex = "^([1-9]\\d{0,12}|0)(\\.\\d{1,2}|\\.\$)?\$|^$".toRegex()

private fun isValidDateFormat(input: String): Boolean {
    return isValidDateFormatRegex.matches(input)
}

private fun isValidAmountFormat(input: String): Boolean {
    return isValidAmountFormatRegex.matches(input) && input.isNotBlank() && (input.toBigDecimalOrNull()
        ?: 0.toBigDecimal()) >= 0.toBigDecimal()
}

private val lengthValidator: FormFieldState.() -> ValidState = {
    val isValid = text.codePointCount(0, text.length) <= 50
    isValid.toValidState("不能大于50字符")
}

private val categoryValidator: FormFieldState.() -> ValidState = {
    val isValid = text.codePointCount(0, text.length) <= 50 && text.isNotBlank()
    isValid.toValidState("类别不能为空或者大于50字符")
}

fun BillEditable.getEditableState(): BillEditableState {
    val billEditable = this
    return BillEditableState(
        amount = FormFieldState(
            text = billEditable.amount,
            validator = { ValidState(isValidAmountFormat(text), "输入无效或金额为负数") }),
        comment = FormFieldState(billEditable.comment, lengthValidator),
        category = FormFieldState(billEditable.category, categoryValidator),
        name = FormFieldState(billEditable.name, lengthValidator),
        datetime = FormFieldState(
            text = billEditable.datetime,
            validator = { ValidState(isValidDateFormat(text), "无效的日期格式") }),
        transactionPartner = FormFieldState(billEditable.transactionPartner, lengthValidator),
        type = FormFieldState(billEditable.type),
        typeInput = FormFieldState(
            getTypeDisplayName(billEditable.type),
            validator = { ValidState(text == "收入" || text == "支出", "必须为收入或支出") }),
        imagesComment = this.imagesComment
    )
}

fun Bill.getEditableState(): BillEditableState {
    val bill = this
    return BillEditableState(
        bill = bill,
        amount = FormFieldState(
            text = bill.amount.toString(),
            validator = { ValidState(isValidAmountFormat(text), "输入无效或金额为负数") }),
        comment = FormFieldState(bill.comment, lengthValidator),
        category = FormFieldState(bill.category, categoryValidator),
        name = FormFieldState(bill.name, lengthValidator),
        datetime = FormFieldState(
            text = bill.datetime.format(dataTimeFormatter),
            validator = { ValidState(isValidDateFormat(text), "无效的日期格式") }),
        transactionPartner = FormFieldState(bill.transactionPartner, lengthValidator),
        type = FormFieldState(bill.type),
        typeInput = FormFieldState(
            getTypeDisplayName(bill.type),
            validator = { ValidState(text == "收入" || text == "支出", "必须为收入或支出") }),
        imagesComment = bill.imagesComment
    )
}


@Composable
fun BillEditDialog(
    billEditableState: BillEditableState,
    billEditAction: BillEditAction,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(dismissOnClickOutside = false),
    onConfirm: () -> Unit,
    onConfirmText: String = "添加",
    moreButton: @Composable (RowScope.() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = AlertDialogDefaults.shape,
            modifier = Modifier
                .wrapContentSize()
        ) {
            Column {
                Box(modifier = Modifier.padding(all = 24.dp)) {
                    Text(
                        text = "账单",
                        fontStyle = MaterialTheme.typography.headlineLarge.fontStyle
                    )
                }
//                Spacer(modifier = Modifier.height(16.dp))
                BillEditPage(billEditableState)
//                Spacer(modifier = Modifier.height(24.dp))

                val context = LocalContext.current
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(all = 24.dp)
                ) {
                    moreButton?.invoke(this)

                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        BillEditActivity.start(context, billEditableState.toBill(), billEditAction)
                    }) {
                        Text(text = "更多")
                    }
                    TextButton(onClick = {
                        onConfirm()
                    }) {
                        Text(text = onConfirmText)
                    }
                }
            }

        }
    }
}

@Composable
fun BillEditPage(
    bill: BillEditableState,
    modifier: Modifier = Modifier
        .padding(24.dp)
        .verticalScroll(
            rememberScrollState()
        )
) {
    Column(
        modifier = modifier
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedFormField(
            state = bill.name,
            label = {
                Text(text = "名称")
            },
            singleLine = true,
        )
        OutlinedFormField(
            state = bill.transactionPartner,
            label = {
                Text(text = if (bill.type.text == Bill.TYPE_OUT) "商户" else "来源")
            },
            singleLine = true,
        )
        Category(bill)
        OutlinedFormField(
            state = bill.amount,
            label = {
                Text(text = "金额")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        DateTimeField(bill)
        OutlinedFormField(
            state = bill.comment,
            label = {
                Text(text = "备注")
            },
            maxLines = 3
        )
        OutlinedFormField(
            state = bill.typeInput,
            label = {
                Text(text = "类型")
            },
            maxLines = 3
        )
    }
}

@Composable
private fun Category(bill: BillEditableState) {

    var isShowDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var categories by remember {
        mutableStateOf<Categories?>(null)
    }
    LaunchedEffect(key1 = isShowDialog, block = {
        if (isShowDialog) {
            categories = CategoryRepository.getAllCategories()
        }
    })
    if (isShowDialog && categories != null) {
        CategoryPickerDialog(
            currentType = bill.type.text,
            currentCategory = bill.category.text,
            categories = categories!!,
            nullCategoriesButtonText = "取消",
            onDismissRequest = { isShowDialog = false },
            onSelected = { _, category0 ->
                isShowDialog = false
                if (category0 != null) {
                    bill.category.text = category0
                }
            }
        )
    }
    OutlinedFormField(
        state = bill.category,
        label = {
            Text(text = "类别")
        },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {
                isShowDialog = true
            }) {
                Icon(painterResource(id = R.drawable.category), contentDescription = "选择类别")
            }
        }
    )
}

@Composable
private fun DateTimeField(bill: BillEditableState) {
    var isShowDatePicker by rememberSaveable {
        mutableStateOf(false)
    }
    var isShowTimePicker by rememberSaveable {
        mutableStateOf(false)
    }
    val initialDateTime = remember(bill.datetime.text) {
        runCatching {
            LocalDateTime.parse(bill.datetime.text, dataTimeFormatter)
        }.getOrDefault(LocalDateTime.now())
    }
    var newLocalDateTime by remember {
        mutableStateOf(LocalDateTime.now())
    }
    if (isShowDatePicker) {
        DatePickerDialog(
            onDismissRequest = { isShowDatePicker = false },
            onDateChange = {
                newLocalDateTime = it.atStartOfDay()
                isShowDatePicker = false
                isShowTimePicker = true
            },
            title = { Text(text = "请选择日期") },
            initialDate = initialDateTime.toLocalDate(),
        )
    }
    if (isShowTimePicker) {
        TimePickerDialog(
            onDismissRequest = { isShowTimePicker = false },
            onTimeChange = { localtime ->
                bill.datetime.apply {
                    newLocalDateTime = newLocalDateTime.withHour(localtime.hour)
                        .withMinute(localtime.minute)
                    text = newLocalDateTime.format(dataTimeFormatter)
                }
                isShowTimePicker = false
            },
            title = { Text(text = "请选择时间") },
            is24HourFormat = true,
            initialTime = initialDateTime.toLocalTime(),
        )
    }
    OutlinedFormField(
        state = bill.datetime,
        label = {
            Text(text = "日期")
        },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {
                isShowDatePicker = true
            }) {
                Icon(
                    painterResource(id = R.drawable.calendar),
                    contentDescription = "选择日期"
                )
            }
        }
    )
}