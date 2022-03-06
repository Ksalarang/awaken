package com.diyartaikenov.app.awaken.ui.presets.session

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.navArgs
import com.diyartaikenov.app.awaken.databinding.ActivitySessionBinding

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding

    private val navArgs: SessionActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemBars()
        supportActionBar?.hide()

        binding.etMinutes.text = navArgs.duration.toString()
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}
