package com.diyartaikenov.app.awaken.ui.presets.session

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val SECOND_IN_MILLIS = 1000L
private const val SECONDS_MAX_VALUE = 59
private const val MINUTE_IN_SECONDS = 60

class SessionTimer(private var initialMinutes: Int) {

    private var _minutes = MutableLiveData(0)
    private var _seconds = MutableLiveData(0)
    private var _timerStarted = MutableLiveData(false)
    private var _timerRunning = MutableLiveData(false)

    val minutes: LiveData<Int> = _minutes
    val seconds: LiveData<Int> = _seconds
    val timerStarted: LiveData<Boolean> = _timerStarted
    val timerRunning: LiveData<Boolean> = _timerRunning

    private var secondsTotal = initialMinutes * MINUTE_IN_SECONDS
    private var timer = createTimer()

    private var minutesTemp = 0
    private var secondsTemp = 0

    fun start() {
        timer.start()
        _timerStarted.value = true
        _timerRunning.value = true
    }

    fun pause() {
        // Save minutes and seconds in temporary variables
        minutesTemp = _minutes.value!!
        secondsTemp = _seconds.value!!

        timer.cancel()
        _timerRunning.value = false
    }

    fun resume() {
        // recalculate total amount of seconds left after a pause
        val secondsPassed = minutesTemp * MINUTE_IN_SECONDS + secondsTemp
        secondsTotal = initialMinutes * MINUTE_IN_SECONDS - secondsPassed

        timer = createTimer().start()
        _timerRunning.value = true
    }

    fun stop() {
        timer.cancel()
        _timerStarted.value = false
        _timerRunning.value = false
    }

    private fun createTimer(): CountDownTimer {
        return object: CountDownTimer(
            secondsTotal * SECOND_IN_MILLIS,
            SECOND_IN_MILLIS
        ) {

            override fun onTick(millisUntilFinished: Long) {
                addSecond()
            }

            override fun onFinish() {
                _timerStarted.value = false
                _timerRunning.value = false
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
