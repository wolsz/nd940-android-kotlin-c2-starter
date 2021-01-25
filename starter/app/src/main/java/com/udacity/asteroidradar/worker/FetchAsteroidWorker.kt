package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.AsteroidsDatabase.Companion.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException

class FetchAsteroidWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val asteroidsRepository = AsteroidsRepository(database)

        return try {
            asteroidsRepository.getNewAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}