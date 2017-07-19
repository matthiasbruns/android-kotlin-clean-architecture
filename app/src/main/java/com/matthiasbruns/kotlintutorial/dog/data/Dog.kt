package com.matthiasbruns.kotlintutorial.dog.data

/**
 * The model class to store dog data in.
 *
 * @param id the id of the dog image
 * @param url the url to the image
 * @param time the timestamp when the images was shot
 * @param format the format the picture is stored in
 */
data class Dog(val id: String, val url: String, val time: String, val format: String)