package com.matthiasbruns.kotlintutorial.config

/**
 * App-white presenter configuration for equal behavior in all presenters.
 */
class PresenterConfig {

    /**
     * The click debounce time in ms for all presenters
     */
    val clickDebounce: Long = 150
}