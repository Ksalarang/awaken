package com.diyartaikenov.app.awaken.ui.presets.session

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.diyartaikenov.app.awaken.R
import com.diyartaikenov.app.awaken.databinding.ActivitySessionBinding
import com.diyartaikenov.app.awaken.ui.presets.*
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionActivityViewModel
import com.diyartaikenov.app.awaken.ui.viewmodel.SessionActivityViewModelFactory
import com.diyartaikenov.app.awaken.utils.Utils

class SessionActivity : AppCompatActivity() {

    private lateinit var viewModel: SessionActivityViewModel
    private lateinit var bind: ActivitySessionBinding
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    private var initialDuration = 0
    private var endTimestamp = 0L

    private var timerState = TimerState.NOT_STARTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(bind.root)

        Utils.hideStatusBars(window.decorView)
        supportActionBar?.hide()

        if (Utils.isOreoOrAbove()) { createNotificationChannel() }

        alertDialogBuilder = buildAlertDialogBuilder()

        // Initialize the viewModel
        val vm: SessionActivityViewModel by viewModels {
            SessionActivityViewModelFactory(applicationContext)
        }
        viewModel = vm

        observeViewModel()

        // Initialize initialDuration with a value received
        // from the fragment that launched this activity.
        initialDuration = intent.getIntExtra(EXTRA_INITIAL_DURATION, 0)

        // Launch the service only if it is not already running
        timerState = viewModel.timerState.value!!
        if (timerState == TimerState.NOT_STARTED) {
            val startServiceIntent = Intent(this, SessionService::class.java)
                .putExtra(EXTRA_SESSION_COMMAND, SessionCommand.START)
                .putExtra(EXTRA_DURATION_MINUTES, initialDuration)
            startSessionService(startServiceIntent)
        }

        bind.apply {
            fabPauseOrResume.setOnClickListener {
                if (timerState == TimerState.RUNNING) {
                    // Save endTimestamp in case the user wants to finish the session
                    // before it ends
                    endTimestamp = System.currentTimeMillis() / MILLIS_IN_SECOND

                    startServiceWithCommand(SessionCommand.PAUSE)
                } else if (timerState == TimerState.PAUSED){
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

    override fun onBackPressed() {
        if (timerState == TimerState.RUNNING) {
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

    private fun observeViewModel() {

        viewModel.minutes.observe(this) { minutes ->
            bind.tvMinutes.update(minutes)

            val minutesLeft = initialDuration - minutes
            bind.sessionInfo.text = resources
                .getQuantityString(R.plurals.minutes_left, minutesLeft, minutesLeft)
        }

        viewModel.seconds.observe(this) { seconds ->
            bind.tvSeconds.update(seconds)
        }

        viewModel.timerState.observe(this) { timerState ->
            this.timerState = timerState

            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (timerState) {

                TimerState.NOT_STARTED -> {} // Don't do anything

                TimerState.RUNNING -> {
                    bind.fabPauseOrResume.setImageResource(R.drawable.ic_pause)
                    bind.fabCloseSession.visibility = View.INVISIBLE
                }

                TimerState.PAUSED -> {
                    bind.apply {
                        fabPauseOrResume.setImageResource(R.drawable.ic_play)

                        if (getMinutes() > 0) {
                            fabCloseSession.setImageResource(R.drawable.ic_done)
                        }
                        fabCloseSession.visibility = View.VISIBLE
                    }
                }

                TimerState.FINISHED -> {
                    // Save endTimestamp in case the user wants to finish the session
                    // before it ends
                    endTimestamp = System.currentTimeMillis() / MILLIS_IN_SECOND

                    onSessionEnd()
                }
            }
        }
    }

    private fun onSessionEnd() {
        bind.apply {
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

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
            }
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

    private fun getMinutes() = bind.tvMinutes.text.toString().toInt()
}
