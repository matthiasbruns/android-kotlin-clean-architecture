package com.matthiasbruns.kotlintutorial.dog.injection

import com.matthiasbruns.kotlintutorial.dog.presentation.DogsListPresenter
import com.matthiasbruns.kotlintutorial.dog.view.DogsListActivity
import com.matthiasbruns.kotlintutorial.dog.view.DogsListFragment
import com.matthiasbruns.kotlintutorial.injection.AppModule
import com.matthiasbruns.kotlintutorial.injection.NetworkModule
import com.matthiasbruns.kotlintutorial.injection.PresentationModule
import dagger.Component
import javax.inject.Singleton

/**
 * This dagger component provides all required dependencies for the dog domain to work.
 */
@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        DogModule::class,
        NetworkModule::class,
        PresentationModule::class
))
interface DogComponent {
    fun inject(activity: DogsListActivity)
    fun inject(fragment: DogsListFragment)
    fun inject(presenter: DogsListPresenter)
}