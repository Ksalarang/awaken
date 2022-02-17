package com.diyartaikenov.app.awaken.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diyartaikenov.app.awaken.data.MeditationPresetDao

class PresetViewModelFactory(
    private val presetDao: MeditationPresetDao
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PresetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PresetViewModel(presetDao) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
