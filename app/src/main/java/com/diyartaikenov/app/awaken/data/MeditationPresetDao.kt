package com.diyartaikenov.app.awaken.data

import androidx.room.*
import com.diyartaikenov.app.awaken.model.MeditationPreset
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for database interaction.
 */
@Dao
interface MeditationPresetDao {
    @Query("select * from meditation_preset_db")
    fun getMeditationPresets(): Flow<List<MeditationPreset>>

    @Query("select * from meditation_preset_db where id = :id")
    fun getMeditationPreset(id: Long): Flow<MeditationPreset>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meditationPreset: MeditationPreset)

    @Update
    suspend fun update(meditationPreset: MeditationPreset)

    @Delete
    suspend fun delete(meditationPreset: MeditationPreset)
}