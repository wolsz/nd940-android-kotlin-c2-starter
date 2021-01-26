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
import com.udacity.asteroidradar.api.todaysDate
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, DONE, ERROR }
enum class Selection { TODAY, NEXT7DAYS, ALLSTORED}

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

    private val database = AsteroidsDatabase.getDatabase(app)
    private val asteroidsRepository = AsteroidsRepository(database)


    val and = asteroidsRepository.asteroidsData
    init {
        viewModelScope.launch {
            asteroidsRepository.getNewAsteroids()
        }

        displayAsteroids(Selection.ALLSTORED)
        getPictureOfTheDay()
//        getAllAsteroids()
    }


//    fun getAllAsteroids() {
//        viewModelScope.launch {
//            try {
//                _status.value = AsteroidApiStatus.LOADING
//                _asteroids.value = asteroidsRepository.getAllAsteroids()
//                _status.value = AsteroidApiStatus.LOADING
//            }  catch (e: Exception) {
//                _status.value = AsteroidApiStatus.ERROR
//                _asteroids.value = ArrayList()
//            }
//
//        }
//    }
//
//    fun getTodayAsteroids() {
//        viewModelScope.launch {
//            _asteroids.value = asteroidsRepository.getTodaysAsteroids()
//        }
//    }
//
//    fun getNextWeeksAsteroids() {
//        viewModelScope.launch {
//            _asteroids.value = asteroidsRepository.getNextWeekAsteroids()
//        }
//    }

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
        val today = todaysDate()

        viewModelScope.launch {
            try {
                _status.value = AsteroidApiStatus.LOADING
                when (asteroidSet) {
                    Selection.ALLSTORED -> _asteroids.value = asteroidsRepository.getAllAsteroids()
                    Selection.NEXT7DAYS -> _asteroids.value = asteroidsRepository.getNextWeekAsteroids()
                    Selection.TODAY -> _asteroids.value = asteroidsRepository.getTodaysAsteroids()
                }

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