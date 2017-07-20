package com.matthiasbruns.kotlintutorial.injection

import android.content.Context
import com.matthiasbruns.kotlintutorial.KotlinApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * This module should be used to inject application scoped dependencies.
 */
@Module
class AppModule(private val application: KotlinApplication) {

    @Singleton
    @Provides fun provideApplication(): KotlinApplication = application

    @Singleton
    @Provides fun provideApplicationContext(): Context {
        return application
    }
}
