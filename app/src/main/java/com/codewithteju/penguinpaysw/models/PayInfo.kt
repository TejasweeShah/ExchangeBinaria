package com.codewithteju.penguinpaysw.models

data class PayInfo(
    val personName: String,
    val CountryName: String,
    val countryAbbr: String,
    val phonePrefix: String,
    val phoneNumber: Int,
    val exchangeRate: Int,
    val amountToTransfer: String
)
