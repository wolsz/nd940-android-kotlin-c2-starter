package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Network.AsteroidApi
import com.udacity.asteroidradar.api.getWeek
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.todaysDate
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.database.AsteroidsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

//    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
//        it
    val asteroidsData: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids()
//    }

    suspend fun getAllAsteroids(): List<Asteroid> {
        return withContext(Dispatchers.IO) {
            database.asteroidDao.getAllAsteroids()
        }
    }

    suspend fun getTodaysAsteroids(): List<Asteroid> {
        val today = todaysDate()
        return withContext(Dispatchers.IO) {
            database.asteroidDao.getTodayAsteroids(today)
        }
    }

    suspend fun getNextWeekAsteroids(): List<Asteroid> {
        val week = getWeek()
        return withContext(Dispatchers.IO) {
            database.asteroidDao.getNextWeekAsteroids(week)
        }
    }

      suspend fun getNewAsteroids() {

        withContext(Dispatchers.IO) {
            val today = todaysDate()
            val resultString = AsteroidApi.retrofitService.getAsteroidProperties(today)
            val asteroids = parseAsteroidsJsonResult(JSONObject(resultString))
            val asteroidsArray = asteroids.toTypedArray()
            database.asteroidDao.insertAll(*asteroidsArray)
        }
    }
}
