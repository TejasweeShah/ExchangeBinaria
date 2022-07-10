package com.codewithteju.penguinpaysw.utils

import kotlin.math.pow

object PPHelpers{

    fun convertBinaryToUSD(numStr: String): Long {
        var num = numStr.toLong()
        var decimalNumber = 0L
        var i = 0
        var remainder: Long

        while (num != 0L) {
            remainder = num % 10
            num /= 10
            decimalNumber += (remainder * 2.0.pow(i.toDouble())).toLong()
            ++i
        }
        return decimalNumber
    }


    fun validName(nameText: String): String? {
        if (!nameText.trim().matches("^[A-Za-z ]+\$".toRegex())) {
            return "Only Letters Allowed"
        }
        return null
    }

    fun validCountry(countryText: String): String? {
        if (countryText.isBlank() || countryText.isEmpty()) {
            return "Select Country"
        }
        return null
    }

    fun validPhone(phoneText: String, phoneLength: Int): String? {
        if (phoneText.length != phoneLength) {
            return "Enter Valid Phone Number"
        }
        return null
    }

    fun validAmount(amountText: String): String? {
        if (amountText.isEmpty() || amountText.isBlank()) {
            return "Enter Valid Amount"
        }
        return null
    }
}