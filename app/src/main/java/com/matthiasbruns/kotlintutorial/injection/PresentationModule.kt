package com.matthiasbruns.kotlintutorial.injection

import com.matthiasbruns.kotlintutorial.config.PresenterConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * This module provides presenter dependencies.
 */
@Module
class PresentationModule {

    @Provides
    @Singleton
    fun providePresenterConfig(): PresenterConfig {
        return PresenterConfig()
    }
}