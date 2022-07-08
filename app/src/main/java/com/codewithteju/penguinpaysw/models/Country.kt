package com.codewithteju.penguinpaysw.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class Country(

    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val currencyAbbr: String,
    val phonePrefix: String,
    val phoneDigits: Int
)