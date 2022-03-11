package com.diyartaikenov.app.awaken.ui.presets.session

import android.app.Notification
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.diyartaikenov.app.awaken.R

class SessionService: LifecycleService() {

    private lateinit var sessionTimer: SessionTimer
    private lateinit var broadcastManager: LocalBroadcastManager

    override fun onCreate() {
        super.onCreate()
        broadcastManager = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.apply {
            when(getSerializableExtra(EXTRA_SESSION_COMMAND)) {

                SessionCommand.START -> {
                    sessionTimer = SessionTimer(
                        getIntExtra(EXTRA_DURATION_MINUTES, 0)
                    )
                    observeSessionState()

                    sessionTimer.start()
                    startForeground(SESSION_NOTIFICATION_ID, buildNotification())
                }

                SessionCommand.RESUME -> sessionTimer.resume()

                SessionCommand.PAUSE -> sessionTimer.pause()

                SessionCommand.STOP -> {
                    sessionTimer.stop()
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun observeSessionState() {
        sessionTimer.minutes.observe(this) { minutes ->
            val minutesChanged = Intent(ACTION_SESSION_STATE_CHANGED)
                .putExtra(EXTRA_RESULT_CODE, MINUTES_RESULT_CODE)
                .putExtra(EXTRA_SESSION_MINUTES, minutes)
            broadcastManager.sendBroadcast(minutesChanged)
        }
        sessionTimer.seconds.observe(this) { seconds ->
            val secondsChanged = Intent(ACTION_SESSION_STATE_CHANGED)
                .putExtra(EXTRA_RESULT_CODE, SECONDS_RESULT_CODE)
                .putExtra(EXTRA_SESSION_SECONDS, seconds)
            broadcastManager.sendBroadcast(secondsChanged)
        }
        sessionTimer.timerStarted.observe(this) { started ->
            val timerStateStarted = Intent(ACTION_SESSION_STATE_CHANGED)
                .putExtra(EXTRA_RESULT_CODE, TIMER_STARTED_RESULT_CODE)
                .putExtra(EXTRA_SESSION_STARTED, started)
            broadcastManager.sendBroadcast(timerStateStarted)
        }
        sessionTimer.timerRunning.observe(this) { running ->
            val timerStateRunning = Intent(ACTION_SESSION_STATE_CHANGED)
                .putExtra(EXTRA_RESULT_CODE, TIMER_RUNNING_RESULT_CODE)
                .putExtra(EXTRA_SESSION_RUNNING, running)
            broadcastManager.sendBroadcast(timerStateRunning)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, SESSION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.title_session_active))
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .build()
    }
}
