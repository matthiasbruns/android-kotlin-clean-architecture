package com.matthiasbruns.kotlintutorial.dog.repository

import android.support.annotation.IntRange
import com.matthiasbruns.kotlintutorial.dog.data.Dog
import io.reactivex.Single

/**
 * The dog repository provides endpoints for the caller to interact with dog data.
 */
interface DogRepository {
    /**
     * Loads a random collection of dogs.
     * @param max the maximum amount of loaded dogs
     * @return a Single which emits a List of dogs
     */
    fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Single<List<Dog>>
}