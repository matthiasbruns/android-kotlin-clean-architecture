package com.matthiasbruns.kotlintutorial

import android.app.Application
import com.matthiasbruns.kotlintutorial.injection.ApplicationComponent
import com.matthiasbruns.kotlintutorial.injection.ApplicationModule
import com.matthiasbruns.kotlintutorial.injection.DaggerApplicationComponent

class KotlinApplication : Application() {

    companion object {
        @JvmStatic lateinit var applicationComponent: ApplicationComponent
    }

    /**
     * Lazy initialized ApplicationComponent
     */
    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Inject application dependencies
        component.inject(this)
        applicationComponent = component
    }
}
