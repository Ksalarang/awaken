package com.diyartaikenov.app.awaken.ui.presets.session

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.utils.Utils

private const val VIBRATION_DURATION = 1000L

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
                    sessionTimer = SessionTimer(getIntExtra(EXTRA_DURATION_MINUTES, 0))
                    observeSessionState()

                    sessionTimer.start()
                    startForeground(SESSION_NOTIFICATION_ID, buildNotification())
                }

                SessionCommand.RESUME -> sessionTimer.resume()

                SessionCommand.PAUSE -> sessionTimer.pause()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        sessionTimer.cancel()
        super.onDestroy()
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

        sessionTimer.timerState.observe(this) { state ->
            val timerState = Intent(ACTION_SESSION_STATE_CHANGED)
                .putExtra(EXTRA_RESULT_CODE, TIMER_STATE_RESULT_CODE)
                .putExtra(EXTRA_SESSION_STATE, state)
            broadcastManager.sendBroadcast(timerState)

            if (state == TimerState.FINISHED) {
                vibratePhone()
                stopSelf()
            }
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, SESSION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.title_session_active))
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .build()
    }

    private fun vibratePhone() {
        if (Utils.isOreoOrAbove()) {
            val vibrationEffect = VibrationEffect
                .createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE)

            if (Utils.isSOrAbove()) {
                val combinedVibration = CombinedVibration.createParallel(vibrationEffect)

                (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                    .vibrate(combinedVibration)
            } else {
                (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                    .vibrate(vibrationEffect)
            }
        } else {
            (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                .vibrate(VIBRATION_DURATION)
        }
    }
}
