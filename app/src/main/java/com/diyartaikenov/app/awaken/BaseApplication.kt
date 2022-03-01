package com.diyartaikenov.app.awaken

import android.app.Application
import com.diyartaikenov.app.awaken.data.AppDatabase

/**
 * An application class that allows for the creation of a singleton instance
 * of the [AppDatabase]
 */
class BaseApplication: Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}