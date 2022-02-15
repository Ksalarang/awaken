package com.diyartaikenov.app.awaken.model

/**
 * Meditation Preset entity to be stored in meditation_preset_db.
 */

data class MeditationPreset(
    val id: Long = 0,
    val name: String,
    val durationInMinutes: Int
)
