package com.diyartaikenov.app.awaken.ui.presets.session

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val SECOND_IN_MILLIS = 1000L
private const val SECONDS_MAX_VALUE = 59

class SessionTimer(private val initialDuration: Int) {

    private var _minutes = MutableLiveData(0)
    private var _seconds = MutableLiveData(0)
    private var _timerStarted = MutableLiveData(false)
    private var _timerResumed = MutableLiveData(false)

    val minutes: LiveData<Int> = _minutes
    val seconds: LiveData<Int> = _seconds
    val timerStarted: LiveData<Boolean> = _timerStarted
    val timerResumed: LiveData<Boolean> = _timerResumed

    private val timer = createTimer()

    fun start() {
        timer.start()
    }

    fun stop() {
        timer.cancel()
    }

    private fun createTimer(): CountDownTimer {
        return object: CountDownTimer(
            initialDuration * 60 * SECOND_IN_MILLIS,
            SECOND_IN_MILLIS
        ) {

            override fun onTick(millisUntilFinished: Long) {
                addSecond()
            }

            override fun onFinish() {

            }
        }
    }

    private fun addSecond() {
        if (_seconds.value != SECONDS_MAX_VALUE) {
            _seconds.value = _seconds.value!! + 1
        } else {
            _seconds.value = 0
            addMinute()
        }
    }

    private fun addMinute() {
        _minutes.value = _minutes.value!! + 1
    }
}
