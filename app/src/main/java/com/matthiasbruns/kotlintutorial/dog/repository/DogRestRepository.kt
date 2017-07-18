package com.matthiasbruns.kotlintutorial.dog.repository

import android.annotation.SuppressLint
import android.support.annotation.IntRange
import com.matthiasbruns.kotlintutorial.dog.Dog
import com.matthiasbruns.kotlintutorial.dog.networking.DogApi
import com.matthiasbruns.kotlintutorial.dog.networking.DogsResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by Bruns on 18.07.2017.
 */
class DogRestRepository : DogRepository {

    private val dogApi: DogApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://thedogapi.co.uk")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        dogApi = retrofit.create(DogApi::class.java)
    }

    @SuppressLint("Range")
    override fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Single<List<Dog>> {
        return dogApi.getRandom(max)
                .map { dogsResponse: DogsResponse ->
                    if (dogsResponse.error != null) {
                        throw RuntimeException(dogsResponse.error)
                    }

                    return@map dogsResponse.data
                }
    }
}
