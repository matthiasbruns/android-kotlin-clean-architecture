package com.matthiasbruns.kotlintutorial.dog.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.matthiasbruns.kotlintutorial.dog.data.Dog

/**
 * This model stores the data required by the view.
 * The view can observice this viewmodel and its properties and react to changes.
 */
class DogsListViewModel : ViewModel() {

    /**
     * Holds a list of dog data which can be observed and changed
     */
    private val dogsLiveData = MutableLiveData<List<Dog>>()

    /**
     * Holds the state of loading
     */
    private val loadingLiveData = MutableLiveData<Boolean>()

    /**
     * Observable ListData of dogs
     */
    fun getDogs(): LiveData<List<Dog>> {
        return dogsLiveData
    }

    /**
     * Indicator to show or hide loading informations
     */
    fun isLoading(): LiveData<Boolean> {
        return loadingLiveData
    }

    /**
     * Sets the dog list contents
     */
    fun setDogs(
            dogsLiveData: List<Dog>) {
        this.dogsLiveData.value = dogsLiveData
    }

    /**
     * Sets the loading state
     */
    fun setLoading(loadingLiveData: Boolean) {
        this.loadingLiveData.value = loadingLiveData
    }
}

