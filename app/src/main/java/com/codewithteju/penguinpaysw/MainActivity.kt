package com.codewithteju.penguinpaysw

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codewithteju.penguinpaysw.databinding.ActivityMainBinding
import com.codewithteju.penguinpaysw.utils.NetworkConnectionLD
import com.codewithteju.penguinpaysw.utils.RequestResult
import com.codewithteju.penguinpaysw.utils.TAG
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var networkStatusManager: NetworkConnectionLD
    private lateinit var mainViewModel: MainViewModel
    lateinit var mainActivityBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkStatusManager = NetworkConnectionLD(application)

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.allCountriesLD.observe(this) {
            val countryNames = it.map { country -> country.name }
            val arrayAdapter = ArrayAdapter(this, R.layout.dropdowm_item, countryNames)
            mainActivityBinding.countryACTextView.setAdapter(arrayAdapter)
            mainActivityBinding.countryACTextView.setOnItemClickListener { adapterView, view, position, id ->
                //val  selectedName = adapterView.getItemAtPosition(position) as String
                val selectedCountry = it[position]
                Log.d(TAG,selectedCountry.name + selectedCountry.phonePrefix)

                mainActivityBinding.phoneNumberContainer.prefixText = selectedCountry.phonePrefix
                mainActivityBinding.phoneNumberContainer.counterMaxLength = selectedCountry.phoneDigits

                val phoneDigits = selectedCountry.phoneDigits
                val fArray = arrayOfNulls<InputFilter>(1)
                fArray[0] = LengthFilter(phoneDigits)
                mainActivityBinding.phoneNumberEditText.filters = fArray
            }
        }

        networkStatusManager.observe(this, Observer{ isConnected ->
            if(isConnected){
                mainViewModel.getLatestRates()
                mainActivityBinding.status.text = "Connected"
                mainActivityBinding.payButton.isEnabled = true
            }
            else{
                mainActivityBinding.status.text = "NOT Connected"
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