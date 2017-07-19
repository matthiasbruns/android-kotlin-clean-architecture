package com.matthiasbruns.kotlintutorial.injection

import com.matthiasbruns.kotlintutorial.KotlinApplication
import dagger.Component

@ForApplication
@Component(modules = arrayOf(AppModule::class))
interface ApplicationComponent {

    fun inject(application: KotlinApplication)
}
