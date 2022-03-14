package com.diyartaikenov.app.awaken.ui.presets.session

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val MILLIS_IN_SECOND = 1000L
private const val SECONDS_MAX_VALUE = 59
private const val SECONDS_IN_MINUTE = 60

class SessionTimer(private val initialMinutes: Int) {

    private var _minutes = MutableLiveData(0)
    private var _seconds = MutableLiveData(0)
    private var _timerStarted = MutableLiveData(false)
    private var _timerRunning = MutableLiveData(false)
    private var _timerIsUp = MutableLiveData(false)

    val minutes: LiveData<Int> = _minutes
    val seconds: LiveData<Int> = _seconds
    val timerStarted: LiveData<Boolean> = _timerStarted
    val timerRunning: LiveData<Boolean> = _timerRunning
    val timerIsUp: LiveData<Boolean> = _timerIsUp

    private var secondsTotal: Int
    private var timer: CountDownTimer

    init {
        secondsTotal = initialMinutes * SECONDS_IN_MINUTE
        timer = createTimer(secondsTotal)
    }

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

        timer = createTimer(secondsTotal).start()
        _timerRunning.value = true
    }

    fun cancel() { timer.cancel() }

    private fun createTimer(secondsInFuture: Int): CountDownTimer {
        return object: CountDownTimer(
            secondsInFuture * MILLIS_IN_SECOND,
//            2000,
            MILLIS_IN_SECOND
        ) {

            override fun onTick(millisUntilFinished: Long) {
                addSecond()
            }

            override fun onFinish() {
                _timerIsUp.value = true
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
