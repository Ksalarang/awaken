package com.diyartaikenov.app.awaken.ui.presets.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.navArgs
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.ActivitySessionBinding
import com.diyartaikenov.app.awaken.utils.Utils

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding

    private val navArgs: SessionActivityArgs by navArgs()

    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var sessionStateReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utils.hideSystemBars(window.decorView)
        supportActionBar?.hide()

        if (Utils.isOreoOrAbove()) {
            createNotificationChannel()
        }

        sessionStateReceiver = createBroadCastReceiver()

        broadcastManager = LocalBroadcastManager.getInstance(this)
        broadcastManager.registerReceiver(
            sessionStateReceiver,
            IntentFilter(ACTION_SESSION_STATE_CHANGED)
        )

        startSessionService()

        binding.apply {
            fabPauseOrContinue.setOnClickListener {
                stopSessionService()
            }
        }
    }

    override fun onDestroy() {
        broadcastManager.unregisterReceiver(sessionStateReceiver)
        super.onDestroy()
    }

    private fun startSessionService() {
        val startServiceIntent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.START)
            .putExtra(EXTRA_SESSION_DURATION_MINUTES, navArgs.duration)

        if (Utils.isOreoOrAbove()) {
            startForegroundService(startServiceIntent)
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

    private fun createBroadCastReceiver() = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                when(getIntExtra(EXTRA_RESULT_CODE, 0)) {
                    MINUTES_RESULT_CODE -> {
                        binding.tvMinutes.text =
                            getIntExtra(EXTRA_SESSION_MINUTES, 0).toString()
                    }
                    SECONDS_RESULT_CODE -> {
                        binding.tvSeconds.text =
                            getIntExtra(EXTRA_SESSION_SECONDS, 0).toString()
                    }
                }
            }
        }
    }
}
