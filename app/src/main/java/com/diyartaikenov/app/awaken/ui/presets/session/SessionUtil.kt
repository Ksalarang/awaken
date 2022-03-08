package com.diyartaikenov.app.awaken.ui.presets.session

const val EXTRA_SESSION_COMMAND = "SESSION_COMMAND"

const val EXTRA_SESSION_DURATION_MINUTES = "SESSION_DURATION_MINUTES"
const val EXTRA_SESSION_DURATION_SECONDS = "SESSION_DURATION_SECONDS"

const val EXTRA_SESSION_STARTED = "SESSION_STARTED"
const val EXTRA_SESSION_RESUMED = "SESSION_RESUMED"

const val SESSION_CHANNEL_ID = "SESSION_CHANNEL_ID"
const val SESSION_NOTIFICATION_ID = 10

enum class SessionCommand {
    START, PAUSE, RESUME, STOP
}