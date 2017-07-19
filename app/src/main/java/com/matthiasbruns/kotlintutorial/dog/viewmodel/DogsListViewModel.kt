package com.matthiasbruns.kotlintutorial.dog.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.matthiasbruns.kotlintutorial.dog.data.Dog

/**
 * Created by Bruns on 19.07.2017.
 */

class DogsListViewModel : ViewModel() {

    private val dogsLiveData = MutableLiveData<List<Dog>>()

    private val loadingLiveData = MutableLiveData<Boolean>()

    fun getDogs(): LiveData<List<Dog>> {
        return dogsLiveData
    }

    fun isLoading(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun setDogs(
            dogsLiveData: List<Dog>) {
        this.dogsLiveData.value = dogsLiveData
    }

    fun setLoading(loadingLiveData: Boolean) {
        this.loadingLiveData.value = loadingLiveData
    }
}

