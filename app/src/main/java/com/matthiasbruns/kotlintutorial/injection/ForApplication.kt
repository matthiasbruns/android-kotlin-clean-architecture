package com.matthiasbruns.kotlintutorial.injection

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForApplication
