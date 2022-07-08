package com.codewithteju.penguinpaysw

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithteju.penguinpaysw.models.Country
import com.codewithteju.penguinpaysw.models.ExchangeRates
import com.codewithteju.penguinpaysw.repository.PenguinPayRepository
import com.codewithteju.penguinpaysw.repository.ReceivingCountryRepository
import com.codewithteju.penguinpaysw.utils.RequestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val ppRepository: PenguinPayRepository, private  val countryRepository: ReceivingCountryRepository) :
    ViewModel() {

    val latestRatesLD : LiveData<RequestResult<ExchangeRates>>
        get() = ppRepository.latestResponse


    fun getLatestRates() {
        viewModelScope.launch {
            ppRepository.getLatestRates()
        }
    }

    fun getAll() : LiveData<List<Country>>{
        return countryRepository.getAll()
    }

    fun getCountries() : LiveData<List<String>>{
        return countryRepository.getCountries()
    }
}