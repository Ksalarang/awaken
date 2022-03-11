package com.diyartaikenov.app.awaken.ui.presets.session

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
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
    private lateinit var alertDialogBuilder: AlertDialog.Builder

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

        alertDialogBuilder = buildAlertDialogBuilder()

        val startServiceIntent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.START)
            .putExtra(EXTRA_DURATION_MINUTES, navArgs.duration)

        sendIntentToService(startServiceIntent)

        binding.apply {
            fabPauseOrContinue.setOnClickListener {
                if (timerRunning) {
                    sendCommandToService(SessionCommand.PAUSE)
                } else if (timerStarted && !timerRunning){
                    sendCommandToService(SessionCommand.RESUME)
                }
            }
            fabStopSession.setOnClickListener {
                sendCommandToService(SessionCommand.STOP)
                finish()
            }
        }
    }

    override fun onDestroy() {
        broadcastManager.unregisterReceiver(sessionStateReceiver)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (timerRunning) {
            sendCommandToService(SessionCommand.PAUSE)
        } else if (timerStarted) {
            if (binding.tvMinutes.text.toString().toInt() < 1) {
                sendCommandToService(SessionCommand.STOP)
                super.onBackPressed()
            } else {
                alertDialogBuilder.show()
            }
        }
    }

    private fun sendIntentToService(serviceIntent: Intent) {
        if (Utils.isOreoOrAbove()) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun sendCommandToService(command: SessionCommand) {
        val intent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, command)
        if (Utils.isOreoOrAbove()) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
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
                        binding.sessionInfo.text = resources
                            .getQuantityString(R.plurals.minutes_left, minutesLeft, minutesLeft)
                    }

                    SECONDS_RESULT_CODE -> {
                        binding.tvSeconds.update(getIntExtra(EXTRA_SESSION_SECONDS, 0))
                    }

                    TIMER_STARTED_RESULT_CODE -> {
                        timerStarted = getBooleanExtra(EXTRA_SESSION_STARTED, false)

                        // When the session's ended
                        if (!timerStarted) {
//                            val minutes = binding.tvMinutes.text.toString().toInt()
                            // todo: hide the pauseOrPlay fab
                            binding.sessionInfo.text = getString(R.string.info_session_ended)
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

    private fun buildAlertDialogBuilder(): AlertDialog.Builder {
        val dialogClickListener = dialogClickListener()
        return AlertDialog.Builder(this)
            .setMessage(getString(R.string.question_quit_without_saving))
            .setPositiveButton(getString(R.string.answer_save), dialogClickListener)
            .setNegativeButton(getString(R.string.answer_quit), dialogClickListener)
    }

    private fun dialogClickListener() = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                // todo: save session data
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
                sendCommandToService(SessionCommand.STOP)
                finish()
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
