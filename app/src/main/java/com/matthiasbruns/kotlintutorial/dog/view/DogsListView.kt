package com.matthiasbruns.kotlintutorial.dog.view

import com.matthiasbruns.kotlintutorial.dog.viewmodel.DogsListViewModel
import io.reactivex.Observable
import net.grandcentrix.thirtyinch.TiView

/**
 * This view connects the view implementation with a presenter.
 */
interface DogsListView : TiView {

    /**
     * Emits items when the user clicks the reload button
     */
    fun getViewModel(): DogsListViewModel

    /**
     * Provides the viewmodel for the presenter
     */
    fun onReloadClick(): Observable<Any>
}