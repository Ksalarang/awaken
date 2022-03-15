package com.diyartaikenov.app.awaken.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.diyartaikenov.app.awaken.model.MeditationSession
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for database interaction with [MeditationSession] entity.
 */
@Dao
interface MeditationSessionDao {

    @Query("select * from sessions")
    fun getSessions(): Flow<List<MeditationSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meditationSession: MeditationSession)
}
