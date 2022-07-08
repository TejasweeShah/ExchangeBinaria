package com.codewithteju.penguinpaysw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codewithteju.penguinpaysw.databinding.ActivityMainBinding
import com.codewithteju.penguinpaysw.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var networkStatusManager: NetworkConnectionLD
    lateinit var mainViewModel: MainViewModel
    lateinit var mainActivityBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkStatusManager = NetworkConnectionLD(application)
        mainActivityBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        networkStatusManager.observe(this, Observer{ isConnected ->
            if(isConnected){
                mainViewModel.getLatestRates()
                mainActivityBinding.statusMessage = "Connected"
                mainActivityBinding.payButton.isEnabled = true

            }
            else{
                mainActivityBinding.statusMessage = "NOT Connected"
                mainActivityBinding.payButton.isEnabled = false
                mainActivityBinding.exchangeInfo.text = ""
            }
            Log.d(TAG,isConnected.toString())
        })



        mainViewModel.latestRatesLD.observe(this, Observer {
            when(it){
                is RequestResult.Success -> {
                    mainActivityBinding.exchangeInfo.text = it.data!!.latestRates["KES"].toString()
                    Log.d("TAG",mainViewModel.latestRatesLD.toString())

                }
                is RequestResult.Error -> {
                    mainActivityBinding.exchangeInfo.text = it.message
                    Log.d("TAG",mainViewModel.latestRatesLD.toString())
                }
            }
        })

    }
}