package com.matthiasbruns.kotlintutorial

import android.app.Application
import com.matthiasbruns.kotlintutorial.injection.AppModule
import com.matthiasbruns.kotlintutorial.injection.ApplicationComponent
import com.matthiasbruns.kotlintutorial.injection.DaggerApplicationComponent

/**
 * Entry point of this app.
 * Initializes dependency injection.
 */
class KotlinApplication : Application() {

    companion object {
        /**
         * The ApplicationComponent for the dependency injection context.
         */
        @JvmStatic private lateinit var appComponent: ApplicationComponent

        /**
         * The AppModule. which was created during the setup
         */
        @JvmStatic private lateinit var appModule: AppModule

        /**
         * Returns the appModule for this application.
         * Use this, if you have a dependency to the AppModule in you Components.
         */
        fun module(): AppModule {
            return appModule
        }

        /**
         * Provides the created ApplicationComponent for this app.
         */
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
