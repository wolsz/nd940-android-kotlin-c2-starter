package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Network.AsteroidApi
import com.udacity.asteroidradar.Network.PictureApi
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.todaysDate
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<String>()

    val status: LiveData<String>
        get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay: LiveData<PictureOfDay>
        get() = _pictureOfTheDay


    init {
        getAsteroids()
        getPictureOfTheDay()
    }

    private fun getPictureOfTheDay() {
        viewModelScope.launch {
            try {
                _pictureOfTheDay.value = PictureApi.retrofitService.getPictureOfTheDay()
                _status.value = "Success: Image retrieved ${_pictureOfTheDay.value!!.mediaType}"
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }

    private fun getAsteroids() {
        val today = todaysDate()
        viewModelScope.launch {
            try {
                val resultString = AsteroidApi.retrofitService.getAstroidProperties(today, today)
                _asteroids.value =
                    parseAsteroidsJsonResult(JSONObject(resultString))
                Log.i("AAA", "${_asteroids.value}")
                Log.i("AAA", "${asteroids.value}")
                _status.value = "Success: Asteroids retrieved ${asteroids.value?.size}"
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }

        }

    }
}