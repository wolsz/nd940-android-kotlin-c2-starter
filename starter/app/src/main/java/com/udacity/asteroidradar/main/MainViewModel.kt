package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.Network.AsteroidApi
import com.udacity.asteroidradar.Network.PictureApi
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.todaysDate
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class AsteroidApiStatus { LOADING, DONE, ERROR }

class MainViewModel(app: Application) : ViewModel() {

    private val _status = MutableLiveData<AsteroidApiStatus>()

    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay: LiveData<PictureOfDay>
        get() = _pictureOfTheDay


    private val _navigateToSelectedAndroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAndroid: LiveData<Asteroid>
        get() = _navigateToSelectedAndroid

    private val database = getDatabase(app)
    private val asteroidsRepository = AsteroidsRepository(database)


    init {
        viewModelScope.launch {
            asteroidsRepository.getNewAsteroids()
        }
        getAsteroids()
        getPictureOfTheDay()
        Log.i("AAA", "${asteroidsRepository.getAnd().value}")
    }


//    val ast = asteroidsRepository.asteroids
//    val code = asteroidsRepository.codes


    private fun getPictureOfTheDay() {
        viewModelScope.launch {
            try {
                _pictureOfTheDay.value = PictureApi.retrofitService.getPictureOfTheDay()
            } catch (e: Exception) {
//                _status.value = "Failure: ${e.message}"
            }
        }
    }

    private fun getAsteroids() {
        val today = todaysDate()

        viewModelScope.launch {
            try {
                _status.value = AsteroidApiStatus.LOADING
                val resultString = AsteroidApi.retrofitService.getAsteroidProperties(today, today)
                _asteroids.value =
                    parseAsteroidsJsonResult(JSONObject(resultString))

                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
                _asteroids.value = ArrayList()
            }

        }
        val ast = asteroidsRepository.getAnd()
        Log.i("AAA", "${ast.value}")

    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAndroid.value = asteroid
    }

    fun displayAndroidDetailsComplete() {
        _navigateToSelectedAndroid.value = null
    }
}