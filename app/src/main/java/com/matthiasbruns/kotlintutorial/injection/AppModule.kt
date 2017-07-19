package com.matthiasbruns.kotlintutorial.injection

import android.content.Context
import com.matthiasbruns.kotlintutorial.KotlinApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Bruns on 18.07.2017.
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
