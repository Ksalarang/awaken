package com.diyartaikenov.app.awaken.ui.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.diyartaikenov.app.awaken.ui.presets.session.*

class SessionActivityViewModel(context: Context): ViewModel() {

    private var _minutes = MutableLiveData(0)
    private var _seconds = MutableLiveData(0)
    private var _timerState = MutableLiveData(TimerState.NOT_STARTED)

    var minutes: LiveData<Int> = _minutes
    val seconds: LiveData<Int> = _seconds
    val timerState: LiveData<TimerState> = _timerState

    private var broadcastManager = LocalBroadcastManager.getInstance(context)
    private var timerStateReceiver: BroadcastReceiver

    init {
        timerStateReceiver = createReceiver()
        broadcastManager.registerReceiver(
            timerStateReceiver,
            IntentFilter(ACTION_SESSION_STATE_CHANGED)
        )
    }

    private fun createReceiver() = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                when(getIntExtra(EXTRA_RESULT_CODE, 0)) {
                    MINUTES_RESULT_CODE ->
                        _minutes.value = getIntExtra(EXTRA_SESSION_MINUTES, 0)

                    SECONDS_RESULT_CODE ->
                        _seconds.value = getIntExtra(EXTRA_SESSION_SECONDS, 0)

                    TIMER_STATE_RESULT_CODE ->
                        _timerState.value = getSerializableExtra(EXTRA_SESSION_STATE) as TimerState
                }
            }
        }
    }

    override fun onCleared() {
        broadcastManager.unregisterReceiver(timerStateReceiver)
        super.onCleared()
    }
}

class SessionActivityViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionActivityViewModel(context) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
