package com.diyartaikenov.app.awaken.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

import com.diyartaikenov.app.awaken.data.Converter

@Entity(tableName = "sessions")
@TypeConverters(Converter::class)
data class MeditationSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "duration_in_minutes")
    val durationInMinutes: Int,
    @ColumnInfo(name = "start_timestamp")
    /** The start date of a session that is stored as a number of seconds since the Unix epoch. */
    val startTimestamp: Date,
    @ColumnInfo(name = "end_timestamp")
    /** The end date of a session that is stored as a number of seconds since the Unix epoch. */
    val endTimestamp: Date
)
