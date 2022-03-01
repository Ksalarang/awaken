package com.diyartaikenov.app.awaken.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class MeditationSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "duration_in_minutes")
    val durationInMinutes: Int,
    @ColumnInfo(name = "start_time_stamp")
    val startTimeStamp: Long,
    @ColumnInfo(name = "end_time_stamp")
    val endTimeStamp: Long
)
