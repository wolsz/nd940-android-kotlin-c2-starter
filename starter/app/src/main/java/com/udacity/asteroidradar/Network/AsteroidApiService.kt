package com.udacity.asteroidradar.Network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofitImageOfTheDay = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()



interface PictureApiService {
    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(
        @Query("api_key") api_key: String = Constants.API_KEY
    ): PictureOfDay
}

object PictureApi {
    val retrofitService: PictureApiService by lazy {
        retrofitImageOfTheDay.create(PictureApiService::class.java)
    }
}


interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidProperties(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("api_key") api_key: String = Constants.API_KEY
    ): String
}

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}