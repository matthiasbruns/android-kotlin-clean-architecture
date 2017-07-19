package com.matthiasbruns.kotlintutorial.dog.view

import com.matthiasbruns.kotlintutorial.dog.viewmodel.DogsListViewModel
import io.reactivex.Observable
import net.grandcentrix.thirtyinch.TiView

/**
 * Created by Bruns on 18.07.2017.
 */

interface DogsListView : TiView {

    fun getViewModel(): DogsListViewModel

    fun onReloadClick(): Observable<Any>
}