package com.codewithteju.exchangebinaria.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codewithteju.exchangebinaria.BuildConfig
import com.codewithteju.exchangebinaria.api.PenguinPayAPI
import com.codewithteju.exchangebinaria.models.ExchangeRates
import com.codewithteju.exchangebinaria.utils.RequestResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PenguinPayRepository @Inject constructor(private val ppAPI: PenguinPayAPI) {

    private val _latestResponse = MutableLiveData<RequestResult<ExchangeRates>>()
    val latestResponse: LiveData<RequestResult<ExchangeRates>>
        get() = _latestResponse

    suspend fun getLatestRates() {
        try {
            _latestResponse.postValue(RequestResult.Loading())
            val response = ppAPI.fetchLatestRates(BuildConfig.APP_ID)
            if (response.isSuccessful && response.body() != null) {
                _latestResponse.postValue(RequestResult.Success(response.body()!!))
            } else if (response.errorBody() != null) {
                _latestResponse.postValue(RequestResult.Error("Error Code : ${response.code()}"))
            } else {
                _latestResponse.postValue(RequestResult.Error("Something went Wrong"))
            }
        } catch (e: Exception) {
            _latestResponse.postValue(RequestResult.Error(""))
            e.printStackTrace()
        }
    }
}