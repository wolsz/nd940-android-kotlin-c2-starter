package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao

interface AsteroidDatabaseDao {
    @Query("select * from asteroids_table")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Query("select * from asteroids_table")
    fun getAllAsteroids(): List<Asteroid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: Asteroid)

    @Query("select codename from asteroids_table")
    fun getCodename(): LiveData<List<String>>

}