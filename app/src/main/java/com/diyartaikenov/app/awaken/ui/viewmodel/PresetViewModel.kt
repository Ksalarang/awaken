package com.diyartaikenov.app.awaken.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diyartaikenov.app.awaken.data.MeditationPresetDao
import com.diyartaikenov.app.awaken.model.MeditationPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Shared [ViewModel] to provide data to the [PresetsFragment]
 * and allow for interaction with the [MeditationPresetDao]
 */
class PresetViewModel(private val presetDao: MeditationPresetDao): ViewModel() {

    val presets: LiveData<List<MeditationPreset>> =
        presetDao.getPresets().asLiveData()

    fun getPresetById(id: Long): LiveData<MeditationPreset> {
        return presetDao.getPreset(id).asLiveData()
    }

    fun addPreset(name: String, duration: Int) {
        val preset = MeditationPreset(name = name, durationInMinutes = duration)
        viewModelScope.launch(Dispatchers.IO) {
            presetDao.insert(preset)
        }
    }

    fun deletePreset(preset: MeditationPreset) {
        viewModelScope.launch {
            presetDao.delete(preset)
        }
    }

    fun updatePreset(preset: MeditationPreset) {
        viewModelScope.launch {
            presetDao.update(preset)
        }
    }
}