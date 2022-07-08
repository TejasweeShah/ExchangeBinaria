package com.codewithteju.penguinpaysw.repository

import androidx.lifecycle.LiveData
import com.codewithteju.penguinpaysw.db.ReceivingCountryDAO
import com.codewithteju.penguinpaysw.models.Country
import javax.inject.Inject

class ReceivingCountryRepository @Inject constructor(private val receivingCountryDAO: ReceivingCountryDAO) {

    fun getAll() : LiveData<List<Country>> {
        return receivingCountryDAO.getAll()
    }

    fun getCountries() : LiveData<List<String>> {
        return receivingCountryDAO.getCountries()
    }
}