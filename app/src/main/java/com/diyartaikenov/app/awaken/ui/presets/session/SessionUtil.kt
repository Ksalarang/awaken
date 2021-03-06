package com.diyartaikenov.app.awaken.ui.presets.session

const val EXTRA_SESSION_COMMAND = "SESSION_COMMAND"

const val EXTRA_DURATION_MINUTES = "SESSION_DURATION_MINUTES"

const val EXTRA_SESSION_MINUTES = "SESSION_MINUTES"
const val EXTRA_SESSION_SECONDS = "SESSION_SECONDS"
const val EXTRA_SESSION_STATE = "SESSION_STATE"

const val EXTRA_RESULT_CODE = "RESULT_CODE"
const val MINUTES_RESULT_CODE = 100
const val SECONDS_RESULT_CODE = 101
const val TIMER_STATE_RESULT_CODE = 104

const val ACTION_SESSION_STATE_CHANGED =
    "com.diyartaikenov.app.awaken.ui.presets.session.SessionActivity.SESSION_STATE_CHANGED"

const val SESSION_CHANNEL_ID = "SESSION_CHANNEL_ID"
const val SESSION_NOTIFICATION_ID = 10

const val tag = "mytag"

enum class SessionCommand {
    START, PAUSE, RESUME
}

enum class TimerState {
    NOT_STARTED, RUNNING, PAUSED, FINISHED
}
