package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Network.AsteroidApi
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
    val asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids()
//    }

    val codes: LiveData<List<String>> = database.asteroidDao.getCodename()

    fun getAnd(): LiveData<List<Asteroid>> {
        return database.asteroidDao.getAsteroids()
    }

     suspend fun getNewAsteroids() {
        val today = todaysDate()

        withContext(Dispatchers.IO) {
            val today = todaysDate()
            val resultString = AsteroidApi.retrofitService.getAsteroidProperties(today, today)
            val asteroids = parseAsteroidsJsonResult(JSONObject(resultString))
            val asteroidsArray = asteroids.toTypedArray()
            database.asteroidDao.insertAll(*asteroidsArray)
        }
    }
}
