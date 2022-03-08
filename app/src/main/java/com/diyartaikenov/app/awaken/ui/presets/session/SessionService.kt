package com.diyartaikenov.app.awaken.ui.presets.session

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.diyartaikenov.app.awaken.R

class SessionService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.apply {
            when(getSerializableExtra(EXTRA_SESSION_COMMAND)) {
                SessionCommand.START -> {
                    startForeground(SESSION_NOTIFICATION_ID, buildNotification())
                    Log.d(tag, "service started")
                }
                SessionCommand.STOP -> {
                    stopSelf()
                    Log.d(tag, "service stop self")
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, SESSION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Meditation session is active.")
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .build()
    }
}