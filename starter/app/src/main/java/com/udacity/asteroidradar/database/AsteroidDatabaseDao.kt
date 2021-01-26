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

    @Query("select * from asteroids_table  order by closeApproachDate desc")
    fun getAllAsteroids(): List<Asteroid>

    @Query("select * from asteroids_table where closeApproachDate = :today order by codename desc")
    fun getTodayAsteroids(today: String): List<Asteroid>

    @Query("select * from asteroids_table where closeApproachDate in(:week) order by closeApproachDate desc")
    fun getNextWeekAsteroids(week: ArrayList<String>): List<Asteroid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: Asteroid)

}