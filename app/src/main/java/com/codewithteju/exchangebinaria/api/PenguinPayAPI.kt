package com.codewithteju.exchangebinaria.api

import com.codewithteju.exchangebinaria.models.ExchangeRates
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PenguinPayAPI {

    @GET("/latest.json")
    suspend fun fetchLatestRates(@Query("app_id") appId: String): Response<ExchangeRates>

}