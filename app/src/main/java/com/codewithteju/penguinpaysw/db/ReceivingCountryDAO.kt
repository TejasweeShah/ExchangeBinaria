package com.codewithteju.penguinpaysw.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codewithteju.penguinpaysw.models.Country

@Dao
interface ReceivingCountryDAO {

    @Query("SELECT * FROM COUNTRIES")
    fun getAll(): LiveData<List<Country>>

    @Query("SELECT ALL name FROM countries")
    fun getCountries(): LiveData<List<String>>

}