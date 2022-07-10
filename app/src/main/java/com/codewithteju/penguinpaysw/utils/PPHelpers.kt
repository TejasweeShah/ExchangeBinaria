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
}