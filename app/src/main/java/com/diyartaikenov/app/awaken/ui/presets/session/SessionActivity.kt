package com.diyartaikenov.app.awaken.ui.presets.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.navigation.navArgs
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.ActivitySessionBinding
import com.diyartaikenov.app.awaken.utils.Utils

const val tag = "myTag"

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding

    private val navArgs: SessionActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utils.hideSystemBars(window.decorView)
        supportActionBar?.hide()

        if (Utils.isOreoOrAbove()) {
            createNotificationChannel()
        }

        startSessionService()

        binding.fabPauseOrContinue.setOnClickListener {
            stopSessionService()
        }
    }

    private fun startSessionService() {
        val startServiceIntent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.START)
            .putExtra(EXTRA_SESSION_DURATION_MINUTES, navArgs.duration)

        if (Utils.isOreoOrAbove()) {
            startForegroundService(startServiceIntent)
            Log.d(tag, "start foreground")
        } else {
            startService(startServiceIntent)
        }
    }

    private fun stopSessionService() {
        val intent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.STOP)

        stopService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            SESSION_CHANNEL_ID,
            getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.channel_description)

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }
}
