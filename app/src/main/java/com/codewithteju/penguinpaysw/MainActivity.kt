package com.codewithteju.penguinpaysw

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codewithteju.penguinpaysw.databinding.ActivityMainBinding
import com.codewithteju.penguinpaysw.utils.RequestResult
import com.codewithteju.penguinpaysw.utils.TAG
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var mainActivityBinding: ActivityMainBinding
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)
        observeNetworkConnectivity()
        observeExchangeRates()
        observeAmountChange()
        setupPayButton()
    }

    private fun observeExchangeRates() {
        mainViewModel.latestRatesLiveData.observe(this) {
            when (it) {
                is RequestResult.Error -> {
                    mainActivityBinding.loadingProgressBar.visibility = View.GONE
                    mainActivityBinding.exchangeInfo.text = it.message
                }
                is RequestResult.Loading -> {
                    mainActivityBinding.loadingProgressBar.visibility = View.VISIBLE
                }
                is RequestResult.Success -> {
                    mainActivityBinding.loadingProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupPayButton() {
        mainActivityBinding.payButton.setOnClickListener {
            if (isFormValid()) {
                setupPaymentData()
                showConfirmationDialog()
                mainViewModel.confirmTransaction()
            }
        }
    }

    private fun showConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setTitle("Confirm!")
            .setMessage("Send Binaria to ${mainViewModel.paymentInfo.personName}?")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(this, "PAYMENT DONE", Toast.LENGTH_LONG).show()
                clearFormOnOK()
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            }

        alertDialog = alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun observeAmountChange() {
        mainActivityBinding.transferAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {
                showExchangeRate(chars.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun observeNetworkConnectivity() {
        mainViewModel.networkStatusManager.observe(this, Observer { isConnected ->
            if (isConnected) {
                setupCountryAdapter()
                mainViewModel.fetchLatestRates()
                mainActivityBinding.payButton.isEnabled = true
                mainActivityBinding.noInternetLayout.visibility = View.GONE
            } else {
                mainActivityBinding.payButton.isEnabled = false
                mainActivityBinding.exchangeInfo.text = ""
                mainActivityBinding.noInternetLayout.visibility = View.VISIBLE
            }
            Log.d(TAG, isConnected.toString())
        })
    }

    private fun setupCountryAdapter() {
        mainViewModel.countriesLiveData.observe(this) {
            val countryNames = it.map { country -> country.name }
            val arrayAdapter = ArrayAdapter(
                this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                countryNames
            )
            mainActivityBinding.countryACTextView.setAdapter(arrayAdapter)

            mainActivityBinding.countryACTextView.setOnItemClickListener { _, _, position, _ ->
                val selectedCountry = it[position]
                mainViewModel.setPaymentCountryInfo(selectedCountry)

                mainActivityBinding.phoneNumberContainer.prefixText = selectedCountry.phonePrefix
                mainActivityBinding.phoneNumberContainer.counterMaxLength =
                    selectedCountry.phoneDigits
                mainActivityBinding.phoneNumberEditText.text?.clear()

                val phoneDigits = selectedCountry.phoneDigits
                val fArray = arrayOfNulls<InputFilter>(1)
                fArray[0] = LengthFilter(phoneDigits)
                mainActivityBinding.phoneNumberEditText.filters = fArray

                showExchangeRate(mainActivityBinding.transferAmountEditText.text.toString())
            }
        }
    }

    private fun showExchangeRate(amountString: String) {
        if (amountString.isNotEmpty()) {
            val amountUSD = mainViewModel.convertBinaryToUSD(amountString)
            mainActivityBinding.amountContainer.helperText = "$amountUSD USD"

            // Show exchange rate
            val exchangeInformation = mainViewModel.getExchangeInformation(amountUSD)
            mainViewModel.setAmountToTransfer(exchangeInformation.first, exchangeInformation.second)
            mainActivityBinding.exchangeInfo.text =
                "Rate:${exchangeInformation.first} \n\n Value: ${exchangeInformation.second}"
        }
    }

    private fun isFormValid(): Boolean {
        with(mainActivityBinding) {
            fullNameContainer.helperText = validName()
            countryContainer.helperText = validCountry()
            phoneNumberContainer.helperText = validPhone()
            amountContainer.helperText = validAmount()
        }
        val validName = mainActivityBinding.fullNameContainer.helperText == null
        val validCountry = mainActivityBinding.countryContainer.helperText == null
        val validPhone = mainActivityBinding.phoneNumberContainer.helperText == null
        val validAmount = mainActivityBinding.amountContainer.helperText == null

        return validName && validCountry && validPhone && validAmount

    }

    private fun setupPaymentData() {
        mainViewModel.setPayeeInfo(
            name = mainActivityBinding.fullNameEditText.text.toString(),
            phone = mainActivityBinding.phoneNumberEditText.text.toString()
        )
        //println(mainViewModel.paymentInfo.toString())
    }

    private fun clearFormOnOK() {
        with(mainActivityBinding) {
            fullNameEditText.text?.clear()
            countryACTextView.text.clear()
            phoneNumberEditText.text?.clear()
            transferAmountEditText.text?.clear()
            exchangeInfo.text = ""
            fullNameContainer.helperText = getString(R.string.required)
            countryContainer.helperText = getString(R.string.required)
            phoneNumberContainer.helperText = getString(R.string.required)
            amountContainer.helperText = getString(R.string.required)
        }
    }

    private fun validName(): String? {
        val nameText = mainActivityBinding.fullNameEditText.text.toString()
        if (!nameText.trim().matches("^[A-Za-z,.'-]+\$".toRegex())) {
            return "Only Letters Allowed"
        }
        return null
    }

    private fun validCountry(): String? {
        val countryText = mainActivityBinding.countryACTextView.text.toString()
        if (countryText.isBlank() || countryText.isEmpty()) {
            return "Select Country"
        }
        return null
    }

    private fun validPhone(): String? {
        val phoneText = mainActivityBinding.phoneNumberEditText.text.toString()
        if (phoneText.length != mainActivityBinding.phoneNumberContainer.counterMaxLength) {
            return "Enter Valid Phone Number"
        }
        return null
    }

    private fun validAmount(): String? {
        val amountText = mainActivityBinding.transferAmountEditText.text.toString()
        if (amountText.isEmpty() || amountText.isBlank()) {
            return "Enter Valid Amount"
        }
        return null
    }
}