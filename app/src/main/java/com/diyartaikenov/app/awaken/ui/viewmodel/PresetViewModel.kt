package com.diyartaikenov.app.awaken.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diyartaikenov.app.awaken.data.MeditationPresetDao
import com.diyartaikenov.app.awaken.model.MeditationPreset

/**
 * Shared [ViewModel] to provide data to the [PresetsFragment]
 * and allow for interaction with the [MeditationPresetDao]
 */
class PresetViewModel(
    private val presetDao: MeditationPresetDao
    ): ViewModel() {

        val presets: LiveData<List<MeditationPreset>> =
            presetDao.getMeditationPresets().asLiveData()
}