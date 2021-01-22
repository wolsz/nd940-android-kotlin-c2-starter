package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Asteroid::class], version = 1, exportSchema = false)
abstract class AsteroidsDatabase: RoomDatabase() {

    abstract val asteroidDao: AsteroidDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: AsteroidsDatabase? = null

        fun getDatabase(context: Context): AsteroidsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidsDatabase::class.java,
                        "asteroids"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}