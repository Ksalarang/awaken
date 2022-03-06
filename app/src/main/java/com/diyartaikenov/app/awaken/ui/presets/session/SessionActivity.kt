package com.diyartaikenov.app.awaken.ui.presets.session

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import com.diyartaikenov.app.awaken.databinding.ActivitySessionBinding
import com.diyartaikenov.app.awaken.utils.Utils

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding

    private val navArgs: SessionActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utils.hideSystemBars(window.decorView)
        supportActionBar?.hide()

        binding.etMinutes.text = navArgs.duration.toString()
    }
}
