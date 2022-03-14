package com.diyartaikenov.app.awaken.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

import com.diyartaikenov.app.awaken.model.MeditationSession

/**
 * Data Access Object for database interaction with [MeditationSession] entity.
 */
@Dao
interface MeditationSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meditationSession: MeditationSession)
}
