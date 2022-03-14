package com.diyartaikenov.app.awaken.ui.presets.session

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val MILLIS_IN_SECOND = 1000L
private const val SECONDS_MAX_VALUE = 59
private const val SECONDS_IN_MINUTE = 60

class SessionTimer(private var initialMinutes: Int) {

    private var _minutes = MutableLiveData(0)
    private var _seconds = MutableLiveData(0)
    private var _timerStarted = MutableLiveData(false)
    private var _timerRunning = MutableLiveData(false)
    private var _timerIsUp = MutableLiveData(true)

    val minutes: LiveData<Int> = _minutes
    val seconds: LiveData<Int> = _seconds
    val timerStarted: LiveData<Boolean> = _timerStarted
    val timerRunning: LiveData<Boolean> = _timerRunning
    val timerIsUp: LiveData<Boolean> = _timerIsUp

    private var secondsTotal = initialMinutes * SECONDS_IN_MINUTE
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
        val secondsPassed = minutesTemp * SECONDS_IN_MINUTE + secondsTemp
        secondsTotal = initialMinutes * SECONDS_IN_MINUTE - secondsPassed

        timer = createTimer().start()
        _timerRunning.value = true
    }

    fun cancel() { timer.cancel() }

    private fun createTimer(): CountDownTimer {
        return object: CountDownTimer(
            secondsTotal * MILLIS_IN_SECOND,
            MILLIS_IN_SECOND
        ) {

            override fun onTick(millisUntilFinished: Long) {
                addSecond()
            }

            override fun onFinish() {
                _timerIsUp.value = false
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
