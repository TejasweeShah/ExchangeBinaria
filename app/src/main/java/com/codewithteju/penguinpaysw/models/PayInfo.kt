package com.codewithteju.penguinpaysw.models

data class PayInfo(
    var personName: String? = null,
    var countryName: String? = null,
    var countryAbbr: String? = null,
    var phonePrefix: String? = null,
    var phoneNumber: String? = null,
    var exchangeRate: Double? = null,
    val amountToTransfer: String ? = null
)
