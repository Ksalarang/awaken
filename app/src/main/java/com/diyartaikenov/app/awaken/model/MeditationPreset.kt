package com.diyartaikenov.app.awaken.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Meditation Preset entity to be stored in meditation_preset_db.
 */
@Entity(tableName = "app_database")
data class MeditationPreset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "duration_in_minutes")
    val durationInMinutes: Int
)
