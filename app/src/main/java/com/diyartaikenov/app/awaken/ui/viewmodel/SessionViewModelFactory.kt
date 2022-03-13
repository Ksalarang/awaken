package com.diyartaikenov.app.awaken.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diyartaikenov.app.awaken.data.MeditationSessionDao

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
