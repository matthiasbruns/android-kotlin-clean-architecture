package com.matthiasbruns.kotlintutorial.dog.networking

import com.matthiasbruns.kotlintutorial.dog.data.Dog

/**
 * Created by Bruns on 18.07.2017.
 */
class DogsResponse(val data: List<Dog>, val count: Int, val error: String?)