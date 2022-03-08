package com.diyartaikenov.app.awaken.utils

import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class Utils {
    companion object {
        /**
         * Hide the status bar and the navigation bar.
         * Also change the system bars' behavior to show transient bars by swipe.
         */
        fun hideSystemBars(windowDecorView: View) {
            val windowInsetsController =
                ViewCompat.getWindowInsetsController(windowDecorView) ?: return

            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        fun isOreoOrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}