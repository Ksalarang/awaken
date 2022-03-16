package com.diyartaikenov.app.awaken.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diyartaikenov.app.awaken.data.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.diyartaikenov.app.awaken.data.MeditationSessionDao
import com.diyartaikenov.app.awaken.model.MeditationSession
import com.diyartaikenov.app.awaken.ui.presets.PresetsFragment
import java.util.*

/**
 * Shared [ViewModel] to provide data to [PresetsFragment] and [StatsFragment]
 * and allow for interaction with the [MeditationSessionDao]
 */
class SessionViewModel(private val sessionDao: MeditationSessionDao): ViewModel() {

    private val converters = Converters()

    val sessions: LiveData<List<MeditationSession>> = sessionDao.getSessions().asLiveData()

    fun addSession(durationInMinutes: Int, startTimestamp: Long, endTimestamp: Long) {
        val session = MeditationSession(
            durationInMinutes = durationInMinutes,
            startTimestamp = converters.timestampToDate(startTimestamp),
            endTimestamp = converters.timestampToDate(endTimestamp)
        )
        viewModelScope.launch(Dispatchers.IO) {
            sessionDao.insert(session)
        }
    }
}
