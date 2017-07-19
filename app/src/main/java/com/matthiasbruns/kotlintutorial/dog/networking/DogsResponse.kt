package com.matthiasbruns.kotlintutorial.dog.networking

import com.matthiasbruns.kotlintutorial.dog.data.Dog

/**
 * This POJO holds the response data for the DogApi requests
 *
 * @param data the actual array of dog objects
 * @param count the amount of dog objects in this response
 * @param error an optional error if the service threw one
 */
class DogsResponse(val data: List<Dog>, val count: Int, val error: String?)