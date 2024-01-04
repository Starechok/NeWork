package com.example.nework.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun ImageView.load(
    url: String,
    errorPlaceholder: Int,
    vararg transforms: BitmapTransformation = emptyArray()
) =
    Glide.with(this)
        .load(url)
        .error(errorPlaceholder)
        .timeout(10_000)
        .transform(*transforms)
        .into(this)

fun ImageView.loadCircleCrop(
    url: String,
    error: Int,
    vararg transforms: BitmapTransformation = emptyArray()
) =
    load(url, error, CircleCrop(), *transforms)

fun ImageView.load(
    uri: Uri,
    errorPlaceholder: Int,
    vararg transforms: BitmapTransformation = emptyArray()
) =
    Glide.with(this)
        .load(uri)
        .error(errorPlaceholder)
        .timeout(10_000)
        .transform(*transforms)
        .into(this)

fun TextInputEditText.pickDate(context: Context) {
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = monthOfYear
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth

        this.setText(
            SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                .format(calendar.time)
        )
    }

    DatePickerDialog(
        context,
        datePicker,
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )
        .show()
}

fun TextInputEditText.pickTime(context: Context) {
    val calendar = Calendar.getInstance()
    val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute

        this.setText(
            SimpleDateFormat("HH:mm", Locale.ROOT)
                .format(calendar.time)
        )
    }

    TimePickerDialog(
        context,
        timePicker,
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )
        .show()
}

fun TextInputLayout.markRequired() {
    hint = "$hint *"
}
