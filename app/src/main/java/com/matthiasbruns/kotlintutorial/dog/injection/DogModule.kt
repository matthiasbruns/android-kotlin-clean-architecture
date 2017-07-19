package com.matthiasbruns.kotlintutorial.dog.injection

import com.matthiasbruns.kotlintutorial.dog.networking.DogApi
import com.matthiasbruns.kotlintutorial.dog.repository.DogRepository
import com.matthiasbruns.kotlintutorial.dog.repository.DogRestRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * The DogModule injects a DogRepository implementation into the target.
 */
@Module
class DogModule {

    /**
     * @param retrofit required to create the repository implementation for the DogRepository
     * @return an implementation of the DogRepository
     */
    @Provides
    @Singleton
    fun provideDogsRepository(retrofit: Retrofit): DogRepository {
        // You can decide by whatever params which repo you want to inject

        return DogRestRepository(retrofit.create(DogApi::class.java))
    }
}



