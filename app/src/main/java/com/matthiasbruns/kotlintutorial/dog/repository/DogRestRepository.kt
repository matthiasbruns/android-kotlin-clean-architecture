package com.matthiasbruns.kotlintutorial.dog.repository

import android.annotation.SuppressLint
import android.support.annotation.IntRange
import com.matthiasbruns.kotlintutorial.dog.data.Dog
import com.matthiasbruns.kotlintutorial.dog.networking.DogApi
import com.matthiasbruns.kotlintutorial.dog.networking.DogsResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * The dog repository provides endpoints for the caller to interact with dog data.
 * This implementation's data source is a rest service.
 */
class DogRestRepository(private val dogApi: DogApi) : DogRepository {

    /**
     * Loads a random collection of dogs.
     * This implementation loads the dogs from a rest service.
     *
     * @param max the maximum amount of loaded dogs
     * @return a Single which emits a List of dogs
     */
    @SuppressLint("Range")
    override fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Single<List<Dog>> {
        // Query the service
        return dogApi.getRandom(max)
                .subscribeOn(Schedulers.io())
                .map { dogsResponse: DogsResponse ->
                    // if there was an error, throw an exception
                    if (dogsResponse.error != null) {
                        throw RuntimeException(dogsResponse.error)
                    }

                    // Return the list of dogs
                    return@map dogsResponse.data
                }
    }
}
