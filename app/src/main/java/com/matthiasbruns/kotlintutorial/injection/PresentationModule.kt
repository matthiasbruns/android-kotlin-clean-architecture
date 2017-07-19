package com.matthiasbruns.kotlintutorial.injection

import com.matthiasbruns.kotlintutorial.config.PresenterConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Bruns on 19.07.2017.
 */
@Module
class PresentationModule {

    @Provides
    @Singleton
    fun providePresenterConfig(): PresenterConfig {
        return PresenterConfig()
    }
}