package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Network.PictureApi
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, DONE, ERROR }
enum class Selection { TODAY, NEXT7DAYS, ALLSTORED }

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

    private val _populateDatabase = MutableLiveData<Boolean>()
    val populateDatabase: LiveData<Boolean>
        get() = _populateDatabase

    private val database = AsteroidsDatabase.getDatabase(app)
    private val asteroidsRepository = AsteroidsRepository(database)


    init {
        displayAsteroids(Selection.ALLSTORED)
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

    fun displayAsteroids(asteroidSet: Selection) {

        viewModelScope.launch {
            try {
                _status.value = AsteroidApiStatus.LOADING
                _populateDatabase.value = false
                when (asteroidSet) {
                    Selection.ALLSTORED -> {
                        _asteroids.value = asteroidsRepository.getAllAsteroids()
                        if (_asteroids.value.isNullOrEmpty()) {
                            _populateDatabase.value = true
                        }
                    }
                    Selection.NEXT7DAYS -> _asteroids.value =
                        asteroidsRepository.getNextWeekAsteroids()
                    Selection.TODAY -> _asteroids.value = asteroidsRepository.getTodaysAsteroids()
                }

                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
                _asteroids.value = ArrayList()
            }

        }
    }

    fun populateDatabase() {
        viewModelScope.launch {
            asteroidsRepository.getNewAsteroids()
            displayAsteroids(Selection.ALLSTORED)
        }

    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAndroid.value = asteroid
    }

    fun displayAndroidDetailsComplete() {
        _navigateToSelectedAndroid.value = null
    }
}