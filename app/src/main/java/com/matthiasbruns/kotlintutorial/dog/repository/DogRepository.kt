package com.matthiasbruns.kotlintutorial.dog.repository

import android.support.annotation.IntRange
import com.matthiasbruns.kotlintutorial.dog.Dog
import io.reactivex.Single

/**
 * Created by Bruns on 18.07.2017.
 */
interface DogRepository {
    fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Single<List<Dog>>
}