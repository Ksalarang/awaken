package com.diyartaikenov.app.awaken.ui.presets.session

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.navArgs
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.ActivitySessionBinding
import com.diyartaikenov.app.awaken.utils.Utils

class SessionActivity : AppCompatActivity() {

    private val navArgs: SessionActivityArgs by navArgs()

    private lateinit var binding: ActivitySessionBinding
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var sessionStateReceiver: BroadcastReceiver

    private var timerStarted = false
    private var timerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utils.hideStatusBars(window.decorView)
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

        val startServiceIntent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.START)
            .putExtra(EXTRA_DURATION_MINUTES, navArgs.duration)

        sendIntentToService(startServiceIntent)

        binding.apply {
            fabPauseOrContinue.setOnClickListener {
                if (timerStarted && timerRunning) {
                    pauseSession()
                } else if (timerStarted && !timerRunning){
                    resumeSession()
                }
            }
            fabStopSession.setOnClickListener {
                val intent = Intent(this@SessionActivity, SessionService::class.java)
                    .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.STOP)
                sendIntentToService(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        broadcastManager.unregisterReceiver(sessionStateReceiver)
        super.onDestroy()
    }

    private fun sendIntentToService(serviceIntent: Intent) {
        if (Utils.isOreoOrAbove()) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun pauseSession() {
        val intent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.PAUSE)
        sendIntentToService(intent)
    }

    private fun resumeSession() {
        val minutes = binding.tvMinutes.text.toString().toInt()
        val seconds = binding.tvSeconds.text.toString().toInt()

        val intent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.RESUME)
            .putExtra(EXTRA_DURATION_MINUTES, minutes)
            .putExtra(EXTRA_DURATION_SECONDS, seconds)
        sendIntentToService(intent)
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
                        val minutes = getIntExtra(EXTRA_SESSION_MINUTES, 0)
                        binding.tvMinutes.update(minutes)
                        val minutesLeft = navArgs.duration - minutes
                        binding.tvMinutesLeft.text = resources
                            .getQuantityString(R.plurals.minutes_left, minutesLeft, minutesLeft)
                    }

                    SECONDS_RESULT_CODE -> {
                        binding.tvSeconds.update(getIntExtra(EXTRA_SESSION_SECONDS, 0))
                    }

                    TIMER_STARTED_RESULT_CODE -> {
                        timerStarted = getBooleanExtra(EXTRA_SESSION_STARTED, false)
                        if (!timerStarted) {
                            binding.tvMinutesLeft.text = getString(R.string.info_session_ended)
                        }
                    }

                    TIMER_RUNNING_RESULT_CODE -> {
                        timerRunning = getBooleanExtra(EXTRA_SESSION_RUNNING, false)
                        if (timerRunning) {
                            binding.fabPauseOrContinue.setImageResource(R.drawable.ic_pause)
                            binding.fabStopSession.visibility = View.INVISIBLE
                        } else {
                            binding.fabPauseOrContinue.setImageResource(R.drawable.ic_play)
                            binding.fabStopSession.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    /**
     * Update the textView so that there is an additional zero before a one-digit value.
     */
    @SuppressLint("SetTextI18n")
    private fun TextView.update(value: Int) {
        if (value < 10) {
            this.text = "0$value"
        } else {
            this.text = value.toString()
        }
    }
}
