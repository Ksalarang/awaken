package com.diyartaikenov.app.awaken.ui

import com.github.mikephil.charting.formatter.ValueFormatter

class DayAxisValueFormatter: ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return when(value) {
            1F -> "Mon"
            2F -> "Tue"
            3F -> "Wen"
            4F -> "Thu"
            5F -> "Fri"
            6F -> "Sat"
            7F -> "Sun"
            else -> "n/a"
        }
    }
}
