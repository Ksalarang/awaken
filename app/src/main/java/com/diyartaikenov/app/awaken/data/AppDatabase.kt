package com.diyartaikenov.app.awaken.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.diyartaikenov.app.awaken.model.MeditationPreset

/**
 * A Room database to persist data for this app.
 * This database stores a [MeditationPreset] entity.
 */
@Database(entities = [MeditationPreset::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun meditationPresetDao(): MeditationPresetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}