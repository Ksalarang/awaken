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
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.ActivitySessionBinding
import com.diyartaikenov.app.awaken.ui.presets.*
import com.diyartaikenov.app.awaken.utils.Utils

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var sessionStateReceiver: BroadcastReceiver
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    private var initialDuration = 0
    private var endTimestamp = 0L

    private var timerStarted = false
    private var timerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utils.hideStatusBars(window.decorView)
        supportActionBar?.hide()

        if (Utils.isOreoOrAbove()) { createNotificationChannel() }

        sessionStateReceiver = createBroadCastReceiver()

        broadcastManager = LocalBroadcastManager.getInstance(this)
        broadcastManager.registerReceiver(
            sessionStateReceiver,
            IntentFilter(ACTION_SESSION_STATE_CHANGED)
        )

        alertDialogBuilder = buildAlertDialogBuilder()

        // Initialize initialDuration with a value received
        // from the fragment that launched this activity.
        initialDuration = intent.getIntExtra(EXTRA_INITIAL_DURATION, 0)
        // Send the initialDuration value to the Session service
        val startServiceIntent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.START)
            .putExtra(EXTRA_DURATION_MINUTES, initialDuration)
        startSessionService(startServiceIntent)

        binding.apply {
            fabPauseOrResume.setOnClickListener {
                if (timerRunning) {
                    startServiceWithCommand(SessionCommand.PAUSE)
                } else if (timerStarted && !timerRunning){
                    startServiceWithCommand(SessionCommand.RESUME)
                }
            }

            fabCloseSession.setOnClickListener {
                stopService(Intent(this@SessionActivity, SessionService::class.java))

                if (getMinutes() < 1) {
                    finish()
                } else {
                    onSessionEnd()
                }
            }

            checkboxDontSave.setOnClickListener {
                if ((it as CheckBox).isChecked) {
                    buttonSaveOrQuit.text = getString(R.string.button_quit)
                } else {
                    buttonSaveOrQuit.text = getString(R.string.button_save)
                }
            }

            buttonSaveOrQuit.setOnClickListener {
                if (checkboxDontSave.isChecked) {
                    finish()
                } else {
                    val sessionData = Intent()
                        .putExtra(EXTRA_ACTUAL_DURATION, getMinutes())
                        .putExtra(EXTRA_END_TIMESTAMP, endTimestamp)
                    setResult(RESULT_CODE_OK, sessionData)
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        broadcastManager.unregisterReceiver(sessionStateReceiver)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (timerRunning) {
            startServiceWithCommand(SessionCommand.PAUSE)
        } else {
            if (getMinutes() < 1) {
                stopService(Intent(this, SessionService::class.java))
                super.onBackPressed()
            } else {
                alertDialogBuilder.show()
            }
        }
    }

    private fun startSessionService(serviceIntent: Intent) {
        if (Utils.isOreoOrAbove()) { startForegroundService(serviceIntent) }
        else { startService(serviceIntent) }
    }

    private fun startServiceWithCommand(command: SessionCommand) {
        val intent = Intent(this, SessionService::class.java)
            .putExtra(EXTRA_SESSION_COMMAND, command)
        startSessionService(intent)
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

                        val minutesLeft = initialDuration - minutes
                        binding.sessionInfo.text = resources
                            .getQuantityString(R.plurals.minutes_left, minutesLeft, minutesLeft)
                    }

                    SECONDS_RESULT_CODE -> {
                        binding.tvSeconds.update(getIntExtra(EXTRA_SESSION_SECONDS, 0))
                    }

                    TIMER_STARTED_RESULT_CODE -> {
                        timerStarted = getBooleanExtra(EXTRA_SESSION_STARTED, false)

                        if (!timerStarted) { onSessionEnd() }
                    }

                    TIMER_RUNNING_RESULT_CODE -> {
                        timerRunning = getBooleanExtra(EXTRA_SESSION_RUNNING, false)

                        binding.apply {
                            if (timerRunning) { // When the timer is resumed
                                fabPauseOrResume.setImageResource(R.drawable.ic_pause)
                                fabCloseSession.visibility = View.INVISIBLE
                            } else if (timerStarted) { // When the timer is paused

                                // Save endTimestamp in case the user wants to finish the session
                                // before it ends
                                endTimestamp = System.currentTimeMillis() / MILLIS_IN_SECOND

                                fabPauseOrResume.setImageResource(R.drawable.ic_play)

                                if (getMinutes() > 0) {
                                    fabCloseSession.setImageResource(R.drawable.ic_done)
                                }
                                fabCloseSession.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onSessionEnd() {
        binding.apply {
            endTimestamp = System.currentTimeMillis() / MILLIS_IN_SECOND
            val minutes = getMinutes()
            sessionInfo.text = resources
                .getQuantityString(R.plurals.session_lasted_minutes, minutes, minutes)
            fabCloseSession.visibility = View.GONE
            fabPauseOrResume.visibility = View.GONE
            buttonSaveOrQuit.visibility = View.VISIBLE
            checkboxDontSave.visibility = View.VISIBLE
        }
    }

    private fun buildAlertDialogBuilder(): AlertDialog.Builder {
        val dialogClickListener = dialogClickListener()

        return AlertDialog.Builder(this)
            .setMessage(getString(R.string.question_quit_without_saving))
            .setPositiveButton(getString(R.string.answer_quit), dialogClickListener)
            .setNegativeButton(getString(R.string.answer_cancel), dialogClickListener)
    }

    private fun dialogClickListener() = DialogInterface.OnClickListener { dialog, which ->
        when (which) {

            DialogInterface.BUTTON_POSITIVE -> {
                dialog.dismiss()
                stopService(Intent(this, SessionService::class.java))
                finish()
            }

            DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
        }
    }

    /**
     * Update the textView so that there is an additional zero before a one-digit value.
     */
    @SuppressLint("SetTextI18n")
    private fun TextView.update(value: Int) {
        if (value < 10) { this.text = "0$value" }
        else { this.text = value.toString() }
    }

    private fun getMinutes() = binding.tvMinutes.text.toString().toInt()
}
