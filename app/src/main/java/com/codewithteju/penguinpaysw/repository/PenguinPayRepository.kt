package com.codewithteju.penguinpaysw.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codewithteju.penguinpaysw.BuildConfig
import com.codewithteju.penguinpaysw.api.PenguinPayAPI
import com.codewithteju.penguinpaysw.models.ExchangeRates
import com.codewithteju.penguinpaysw.utils.RequestResult
import javax.inject.Inject

class PenguinPayRepository @Inject constructor( private val ppAPI : PenguinPayAPI) {

    private val _latestResponse = MutableLiveData<RequestResult<ExchangeRates>>()
    val latestResponse: LiveData<RequestResult<ExchangeRates>>
        get() = _latestResponse

    suspend fun getLatestRates() {
        val response = ppAPI.fetchLatestRates(BuildConfig.APP_ID)
        if(response.isSuccessful && response.body() != null) {
            _latestResponse.postValue(RequestResult.Success(response.body()!!))
            Log.d("PP",response.body().toString())
        }
        else if(response.errorBody()!= null){
            _latestResponse.postValue(RequestResult.Error("Error Code : ${response.code()}"))
            Log.d("PP",response.errorBody().toString())
            Log.d("PP",response.code().toString())
        }
        else{
            _latestResponse.postValue(RequestResult.Error("Something went Wrong"))
            Log.d("PP","Something went wrong")
        }
    }
}