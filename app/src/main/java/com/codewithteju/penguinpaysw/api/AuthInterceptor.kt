package com.codewithteju.penguinpaysw.api

import com.codewithteju.penguinpaysw.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val newUrl = chain.request().url.newBuilder().addQueryParameter("app_id",BuildConfig.APP_ID).build()
        val newRequest = chain.request().newBuilder().url(newUrl)
        return chain.proceed(newRequest.build())
    }
}