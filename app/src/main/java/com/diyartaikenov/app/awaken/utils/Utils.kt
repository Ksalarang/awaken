package com.diyartaikenov.app.awaken.utils

import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object Utils {

    /**
     * Hide the status bar and change the system bars' behavior
     * to show transient bars by swipe.
     */
    fun hideStatusBars(windowDecorView: View) {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(windowDecorView) ?: return

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
    }

    /**
     * Check if the SDK level the current device is running is equal to
     * or higher than Android 8 (API level 26)
     */
    fun isOreoOrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /**
     * Check if the SDK level the current device is running is equal to
     * or higher than Android 12 (API level 31)
     */
    fun isSOrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}
