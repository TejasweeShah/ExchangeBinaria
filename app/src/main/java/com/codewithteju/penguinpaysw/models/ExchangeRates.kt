package com.codewithteju.penguinpaysw.models

import com.google.gson.annotations.SerializedName

data class ExchangeRates(

    val timestamp: Long,
    val base: String,

    @field:SerializedName("rates")
    val latestRates: Map<String, Double>
)