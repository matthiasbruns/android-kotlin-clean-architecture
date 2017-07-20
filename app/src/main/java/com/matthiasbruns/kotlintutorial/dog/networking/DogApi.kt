package com.matthiasbruns.kotlintutorial.dog.networking

import android.support.annotation.IntRange
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This service is based on the dogs api
 * http://docs.dogapi1.apiary.io/#reference/0/get-dog/get-a-random-dog
 */
interface DogApi {

    /**
     * You can request dogs from there - the limit is maxed to 20 per request
     */
    @GET("/api/v1/dog")
    fun getRandom(@Query("limit") @IntRange(from = 1, to = 20) limit: Int): Single<DogsResponse>
}