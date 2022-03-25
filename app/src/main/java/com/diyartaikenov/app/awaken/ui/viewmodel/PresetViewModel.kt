package com.diyartaikenov.app.awaken.ui.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.diyartaikenov.app.awaken.data.MeditationPresetDao
import com.diyartaikenov.app.awaken.model.MeditationPreset
import com.diyartaikenov.app.awaken.ui.presets.PresetsFragment
import com.diyartaikenov.app.awaken.ui.presets.AddPresetFragment

/**
 * Shared [ViewModel] to provide data to [PresetsFragment] and [AddPresetFragment]
 * and allow for interaction with the [MeditationPresetDao]
 */
class PresetViewModel(private val presetDao: MeditationPresetDao): ViewModel() {

    var shouldAddDefaultPresets = true

    val presets: LiveData<List<MeditationPreset>> =
        presetDao.getPresets().asLiveData()

    fun getPresetById(id: Long): LiveData<MeditationPreset> {
        return presetDao.getPreset(id).asLiveData()
    }

    fun addPreset(name: String, duration: Int) {
        shouldAddDefaultPresets = false
        val preset = MeditationPreset(name = name, durationInMinutes = duration)
        viewModelScope.launch(Dispatchers.IO) {
            presetDao.insert(preset)
        }
    }

    fun updatePreset(id: Long, presetName: String, duration: Int) {
        val preset = MeditationPreset(
            id = id,
            name = presetName,
            durationInMinutes = duration
        )
        viewModelScope.launch {
            presetDao.update(preset)
        }
    }

    fun deletePreset(preset: MeditationPreset) {
        shouldAddDefaultPresets = false
        viewModelScope.launch {
            presetDao.delete(preset)
        }
    }
}

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
