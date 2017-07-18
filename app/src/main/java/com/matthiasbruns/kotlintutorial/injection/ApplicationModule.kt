package com.matthiasbruns.kotlintutorial.injection

import com.matthiasbruns.kotlintutorial.KotlinApplication
import dagger.Module
import dagger.Provides

/**
 * Created by Bruns on 18.07.2017.
 */
@Module
class ApplicationModule(val application: KotlinApplication) {

    @Provides fun provideApplication(): KotlinApplication = application
}
