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
                if (getMinutes() < 1) {
                    stopService(Intent(this@SessionActivity, SessionService::class.java))
                    finish() //fixme: should navigate back with arguments
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
                    finish() // fixme: should navigate back with arguments
                } else {
                    // todo: save session data
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
        if (Utils.isOreoOrAbove()) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
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

                        val minutesLeft = navArgs.duration - minutes
                        binding.sessionInfo.text = resources
                            .getQuantityString(R.plurals.minutes_left, minutesLeft, minutesLeft)
                    }

                    SECONDS_RESULT_CODE -> {
                        binding.tvSeconds.update(getIntExtra(EXTRA_SESSION_SECONDS, 0))
                    }

                    TIMER_STARTED_RESULT_CODE -> {
                        timerStarted = getBooleanExtra(EXTRA_SESSION_STARTED, false)

                        if (!timerStarted) {
                            onSessionEnd()
//                            binding.tvMinutes.update(1) //fixme
                        }
                    }

                    TIMER_RUNNING_RESULT_CODE -> {
                        timerRunning = getBooleanExtra(EXTRA_SESSION_RUNNING, false)

                        if (timerRunning) {
                            binding.fabPauseOrResume.setImageResource(R.drawable.ic_pause)
                            binding.fabCloseSession.visibility = View.INVISIBLE
                        } else if (timerStarted) {
                            binding.fabPauseOrResume.setImageResource(R.drawable.ic_play)
                            binding.fabCloseSession.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun onSessionEnd() {
        binding.apply {
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
                finish() // fixme: should navigate back with arguments
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
        if (value < 10) {
            this.text = "0$value"
        } else {
            this.text = value.toString()
        }
    }

    private fun getMinutes() = binding.tvMinutes.text.toString().toInt()
}
