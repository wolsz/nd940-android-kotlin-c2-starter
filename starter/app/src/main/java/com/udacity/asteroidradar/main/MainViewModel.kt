package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Network.AstroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _response = MutableLiveData<String>()

    val response: LiveData<String>
    get() = _response

    init {
        getAsteroids()
    }

    private fun getAsteroids() {
        AstroidApi.retrofitService.getAstroidProperties("2021-01-12", "2021-01-12").enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
//                val asteriods: ArrayList<Asteroid> = parseAsteroidsJsonResult(JSONObject(response.body()!!))
                _response.value = "Success: Asteroids retrieved"
                response.body()?.let { Log.i("MAINVIEW MODEL", it) }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _response.value = "Failure: " + t.message
            }

        })
    }
}