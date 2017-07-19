package com.matthiasbruns.kotlintutorial.dog.injection

import com.matthiasbruns.kotlintutorial.dog.networking.DogApi
import com.matthiasbruns.kotlintutorial.dog.repository.DogRepository
import com.matthiasbruns.kotlintutorial.dog.repository.DogRestRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by Bruns on 19.07.2017.
 */
@Module
class DogModule {

    @Provides
    @Singleton
    fun provideDogsRepository(retrofit: Retrofit): DogRepository {
        // You can decide by whatever params which repo you want to inject

        return DogRestRepository(retrofit.create(DogApi::class.java))
    }
}



