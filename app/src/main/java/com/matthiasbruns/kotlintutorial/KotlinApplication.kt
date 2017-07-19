package com.matthiasbruns.kotlintutorial

import android.app.Application
import com.matthiasbruns.kotlintutorial.injection.AppModule
import com.matthiasbruns.kotlintutorial.injection.ApplicationComponent
import com.matthiasbruns.kotlintutorial.injection.DaggerApplicationComponent

class KotlinApplication : Application() {

    companion object {
        @JvmStatic private lateinit var appComponent: ApplicationComponent
        @JvmStatic private lateinit var appModule: AppModule

        fun module(): AppModule {
            return appModule
        }

        fun component(): ApplicationComponent {
            return appComponent
        }
    }

    /**
     * Lazy initialized ApplicationComponent
     */
    private val component: ApplicationComponent by lazy {
        appModule = AppModule(this)
        DaggerApplicationComponent.builder()
                .appModule(appModule)
                .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Inject application dependencies
        component.inject(this)
        appComponent = component
    }
}
