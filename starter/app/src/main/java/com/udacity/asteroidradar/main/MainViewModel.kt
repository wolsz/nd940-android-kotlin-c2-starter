package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Network.AsteroidApi
import com.udacity.asteroidradar.Network.PictureApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.todaysDate
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<String>()

    val status: LiveData<String>
    get() = _status

    init {
        getAsteroids()
        getPictureOfTheDay()
    }

    private fun getPictureOfTheDay() {
        viewModelScope.launch {
            try {
                val pictureOfTheDay = PictureApi.retrofitService.getPictureOfTheDay()
                _status.value = "Success: Image retrieved ${pictureOfTheDay.url}"
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
                val asteroids: ArrayList<Asteroid> = parseAsteroidsJsonResult(JSONObject(resultString))
                _status.value = "Success: Asteroids retrieved ${asteroids.size}"
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }

        }

    }
}