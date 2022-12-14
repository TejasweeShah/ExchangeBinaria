package com.codewithteju.exchangebinaria.di


import com.codewithteju.exchangebinaria.BuildConfig
import com.codewithteju.exchangebinaria.api.PenguinPayAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun providesPenguinPayAPI(retrofit: Retrofit): PenguinPayAPI {
        return retrofit.create(PenguinPayAPI::class.java)
    }
}