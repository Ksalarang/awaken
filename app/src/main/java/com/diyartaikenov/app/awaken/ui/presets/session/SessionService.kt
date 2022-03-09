package com.diyartaikenov.app.awaken.ui.presets.session

import android.app.Notification
import android.content.Intent
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
                        getIntExtra(EXTRA_SESSION_DURATION_MINUTES, 0)
                    )
                    observeSessionState()

                    sessionTimer.start()
                    startForeground(SESSION_NOTIFICATION_ID, buildNotification())
                }

                SessionCommand.STOP -> {
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        sessionTimer.stop()
        super.onDestroy()
    }

    private fun observeSessionState() {
        sessionTimer.minutes.observe(this@SessionService) { minutes ->
            val sendMinutesIntent = Intent(ACTION_SESSION_STATE_CHANGED)
                .putExtra(EXTRA_RESULT_CODE, MINUTES_RESULT_CODE)
                .putExtra(EXTRA_SESSION_MINUTES, minutes)
            broadcastManager.sendBroadcast(sendMinutesIntent)
        }
        sessionTimer.seconds.observe(this@SessionService) { seconds ->
            val sendSecondsIntent = Intent(ACTION_SESSION_STATE_CHANGED)
                .putExtra(EXTRA_RESULT_CODE, SECONDS_RESULT_CODE)
                .putExtra(EXTRA_SESSION_SECONDS, seconds)
            broadcastManager.sendBroadcast((sendSecondsIntent))
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, SESSION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Meditation session is active.")
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .build()
    }
}