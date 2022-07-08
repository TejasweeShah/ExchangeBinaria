package com.codewithteju.penguinpaysw.di

import com.codewithteju.penguinpaysw.BuildConfig
import com.codewithteju.penguinpaysw.api.AuthInterceptor
import com.codewithteju.penguinpaysw.api.PenguinPayAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit.Builder{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
    }

    @Singleton
    @Provides
    fun providesOKHttpClient(authInterceptor: AuthInterceptor) : OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    }


    @Singleton
    @Provides
    fun providesPenguinPayAPI(retrofitBuilder : Retrofit.Builder, okHttpClient: OkHttpClient): PenguinPayAPI {
        return retrofitBuilder
            .client(okHttpClient)
            .build().create(PenguinPayAPI::class.java)
    }
}