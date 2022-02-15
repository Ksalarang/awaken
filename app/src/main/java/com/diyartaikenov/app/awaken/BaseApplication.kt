package com.diyartaikenov.app.awaken

import android.app.Application
import com.diyartaikenov.app.awaken.data.MeditationPresetDatabase

/**
 * An application class that allows for the creation of a singleton instance
 * of the [MeditationPresetDatabase]
 */
class BaseApplication: Application() {

    val database: MeditationPresetDatabase by lazy {
        MeditationPresetDatabase.getDatabase(this)
    }
}