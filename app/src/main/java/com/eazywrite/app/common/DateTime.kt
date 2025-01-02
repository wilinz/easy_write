package com.eazywrite.app.common

import androidx.fragment.app.FragmentManager
import com.eazywrite.app.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

fun pickDate(manager: FragmentManager, currentTimestamp: Long, onDateSelected: (Long) -> Unit) {

    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("请选择日期")
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .setSelection(currentTimestamp)
            .build()
            .apply {
                addOnPositiveButtonClickListener { timestamp ->
                    onDateSelected(timestamp)
                }
            }
    datePicker.show(manager, "")

}

fun pickTime(
    manager: FragmentManager,
    hour: Int = 12,
    minute: Int = 0,
    onDateSelected: (hour: Int, minute: Int) -> Unit
) {
    val picker =
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
//            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("请选择时间")
            .build()

    picker.addOnPositiveButtonClickListener {
        onDateSelected(picker.hour, picker.minute)
    }

    picker.show(manager, "")

}