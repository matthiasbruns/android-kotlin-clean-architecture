package com.matthiasbruns.kotlintutorial.injection

import com.matthiasbruns.kotlintutorial.KotlinApplication
import dagger.Component

/**
 * This component should be used to inject application scoped dependencies.
 */
@ForApplication
@Component(modules = arrayOf(AppModule::class))
interface ApplicationComponent {

    fun inject(application: KotlinApplication)
}
