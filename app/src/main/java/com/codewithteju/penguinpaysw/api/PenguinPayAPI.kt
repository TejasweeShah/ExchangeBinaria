package com.codewithteju.penguinpaysw.api

import com.codewithteju.penguinpaysw.models.ExchangeRates
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PenguinPayAPI {

    @GET("/latest.json")
    suspend fun fetchLatestRates() : Response<ExchangeRates>

}