package com.matthiasbruns.kotlintutorial.dog.repository

import android.annotation.SuppressLint
import android.support.annotation.IntRange
import com.matthiasbruns.kotlintutorial.dog.data.Dog
import com.matthiasbruns.kotlintutorial.dog.networking.DogApi
import com.matthiasbruns.kotlintutorial.dog.networking.DogsResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by Bruns on 18.07.2017.
 */
class DogRestRepository(private val dogApi: DogApi) : DogRepository {

    @SuppressLint("Range")
    override fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Single<List<Dog>> {
        return dogApi.getRandom(max)
                .subscribeOn(Schedulers.io())
                .map { dogsResponse: DogsResponse ->
                    if (dogsResponse.error != null) {
                        throw RuntimeException(dogsResponse.error)
                    }

                    return@map dogsResponse.data
                }
    }
}
