package com.udacity.asteroidradar.main

import android.app.Application
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


    init {
        getAsteroids()
        getPictureOfTheDay()
    }

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

    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAndroid.value = asteroid
    }

    fun displayAndroidDetailsComplete() {
        _navigateToSelectedAndroid.value = null
    }
}