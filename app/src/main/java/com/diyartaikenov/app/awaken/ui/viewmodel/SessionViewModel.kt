package com.diyartaikenov.app.awaken.ui.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

import com.diyartaikenov.app.awaken.data.Converter
import com.diyartaikenov.app.awaken.data.MeditationSessionDao
import com.diyartaikenov.app.awaken.model.MeditationSession
import com.diyartaikenov.app.awaken.ui.presets.PresetsFragment

/**
 * Shared [ViewModel] to provide data to [PresetsFragment] and [StatsFragment]
 * and allow for interaction with the [MeditationSessionDao]
 */
class SessionViewModel(private val sessionDao: MeditationSessionDao): ViewModel() {

    val sessions: LiveData<List<MeditationSession>> = sessionDao.getSessions().asLiveData()

    private val converter = Converter()

    fun addSession(durationInMinutes: Int, startTimestamp: Long, endTimestamp: Long) {
        val session = MeditationSession(
            durationInMinutes = durationInMinutes,
            startTimestamp = converter.timestampToDate(startTimestamp),
            endTimestamp = converter.timestampToDate(endTimestamp)
        )
        viewModelScope.launch(Dispatchers.IO) {
            sessionDao.insert(session)
        }
    }
}

class SessionViewModelFactory(
    private val sessionDao: MeditationSessionDao
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(sessionDao) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
