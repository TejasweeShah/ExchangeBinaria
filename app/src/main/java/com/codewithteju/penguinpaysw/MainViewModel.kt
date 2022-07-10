package com.codewithteju.penguinpaysw

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithteju.penguinpaysw.models.Country
import com.codewithteju.penguinpaysw.models.ExchangeRates
import com.codewithteju.penguinpaysw.models.PayInfo
import com.codewithteju.penguinpaysw.repository.PenguinPayRepository
import com.codewithteju.penguinpaysw.repository.ReceivingCountryRepository
import com.codewithteju.penguinpaysw.utils.NetworkConnectionLD
import com.codewithteju.penguinpaysw.utils.PPHelpers
import com.codewithteju.penguinpaysw.utils.RequestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ppRepository: PenguinPayRepository,
    private val countryRepository: ReceivingCountryRepository,
    val networkStatusManager: NetworkConnectionLD
) : ViewModel() {
    var paymentInfo = PayInfo()

    val latestRatesLiveData: LiveData<RequestResult<ExchangeRates>>
        get() = ppRepository.latestResponse

    val countriesLiveData: LiveData<List<Country>>
        get() = getCountriesList()

    fun fetchLatestRates() {
        viewModelScope.launch {
            ppRepository.getLatestRates()
        }
    }

    private fun getCountriesList(): LiveData<List<Country>> {
        return countryRepository.getAll()
    }

    fun setPaymentCountryInfo(country: Country) {
        paymentInfo = paymentInfo.copy(
            countryName = country.name,
            countryAbbr = country.currencyAbbr,
            phonePrefix = country.phonePrefix,
            phoneDigits = country.phoneDigits
        )
    }

    fun setPayeeInfo(name: String, phone: String) {
        paymentInfo = paymentInfo.copy(
            personName = name.trim(),
            phoneNumber = phone
        )
    }

    fun setAmountToTransfer(exchangeRate: Double, amount: String) {
        paymentInfo = paymentInfo.copy(
            exchangeRate = exchangeRate,
            amountToTransfer = amount
        )
    }

    fun confirmTransaction() {
        // TODO: Imaginary API call for sending Payment
    }


    fun convertBinaryToUSD(amountBinaria: String) = PPHelpers.convertBinaryToUSD(amountBinaria)

    fun getExchangeInformation(amountUSD: Long): Pair<Double, String> {
        paymentInfo.countryAbbr?.let { country ->
            latestRatesLiveData.value?.data?.latestRates?.let { rates ->
                val countryRate = rates[country]
                countryRate?.let {
                    val exchangeRate = countryRate.toInt() * amountUSD
                    return Pair(countryRate, Integer.toBinaryString(exchangeRate.toInt()))
                }
            }
        }
        return Pair(0.0, "0")
    }
}