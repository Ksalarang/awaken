package com.diyartaikenov.app.awaken.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.diyartaikenov.app.awaken.data.MeditationSessionDao
import com.diyartaikenov.app.awaken.model.MeditationSession
import com.diyartaikenov.app.awaken.ui.presets.PresetsFragment

/**
 * Shared [ViewModel] to provide data to [PresetsFragment] and [StatsFragment]
 * and allow for interaction with the [MeditationSessionDao]
 */
class SessionViewModel(private val sessionDao: MeditationSessionDao): ViewModel() {

    fun addSession(durationInMinutes: Int, startTimeStamp: Long, endTimeStamp: Long) {
        val session = MeditationSession(
            durationInMinutes = durationInMinutes,
            startTimeStamp = startTimeStamp,
            endTimeStamp = endTimeStamp
        )
        viewModelScope.launch(Dispatchers.IO) {
            sessionDao.insert(session)
        }
    }
}
