package com.diyartaikenov.app.awaken.data

import androidx.room.TypeConverter
import java.util.*

private const val MILLIS_IN_SECOND = 1000L

class Converters {
    @TypeConverter
    fun timestampToDate(value: Long) = Date(value * MILLIS_IN_SECOND)

    @TypeConverter
    fun dateToTimestamp(date: Date) = date.time / MILLIS_IN_SECOND
}
