package com.codewithteju.exchangebinaria.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codewithteju.exchangebinaria.models.Country

@Database(entities = [Country::class], version = 1)
abstract class PenguinPayDB : RoomDatabase() {
    abstract fun receivingCountryDao(): ReceivingCountryDAO
}