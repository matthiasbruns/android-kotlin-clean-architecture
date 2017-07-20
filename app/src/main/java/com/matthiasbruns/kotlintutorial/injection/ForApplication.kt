package com.matthiasbruns.kotlintutorial.injection

import javax.inject.Qualifier

/**
 * This annotation marks a dagger injection as application-wide
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForApplication
