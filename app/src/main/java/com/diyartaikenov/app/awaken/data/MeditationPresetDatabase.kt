package com.diyartaikenov.app.awaken.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.diyartaikenov.app.awaken.model.MeditationPreset

/**
 * A Room database to persist data for the Awaken app.
 * This database stores a [MeditationPreset] entity.
 */
@Database(entities = [MeditationPreset::class], version = 1, exportSchema = false)
abstract class MeditationPresetDatabase: RoomDatabase() {
    abstract fun meditationPresetDao(): MeditationPresetDao

    companion object {
        @Volatile
        private var INSTANCE: MeditationPresetDatabase? = null

        fun getDatabase(context: Context): MeditationPresetDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    MeditationPresetDatabase::class.java,
                    "meditation_preset_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}