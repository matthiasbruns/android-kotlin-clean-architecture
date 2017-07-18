package com.matthiasbruns.kotlintutorial.injection

import com.matthiasbruns.kotlintutorial.KotlinApplication
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun application(): KotlinApplication

    fun inject(application: KotlinApplication)
}
