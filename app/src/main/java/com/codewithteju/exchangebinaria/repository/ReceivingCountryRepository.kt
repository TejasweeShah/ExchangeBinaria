package com.codewithteju.exchangebinaria.repository

import androidx.lifecycle.LiveData
import com.codewithteju.exchangebinaria.db.ReceivingCountryDAO
import com.codewithteju.exchangebinaria.models.Country
import javax.inject.Inject

class ReceivingCountryRepository @Inject constructor(private val receivingCountryDAO: ReceivingCountryDAO) {

    fun getAll(): LiveData<List<Country>> {
        return receivingCountryDAO.getAll()
    }

    fun getCountries(): LiveData<List<String>> {
        return receivingCountryDAO.getCountries()
    }
}