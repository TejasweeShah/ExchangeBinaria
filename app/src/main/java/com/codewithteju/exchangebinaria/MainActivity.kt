package com.codewithteju.exchangebinaria

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
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.codewithteju.exchangebinaria.databinding.ActivityMainBinding
import com.codewithteju.exchangebinaria.utils.PPHelpers.validAmount
import com.codewithteju.exchangebinaria.utils.PPHelpers.validCountry
import com.codewithteju.exchangebinaria.utils.PPHelpers.validName
import com.codewithteju.exchangebinaria.utils.PPHelpers.validPhone
import com.codewithteju.exchangebinaria.utils.RequestResult
import com.codewithteju.exchangebinaria.utils.TAG
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mainViewModel.paymentInfo.phonePrefix?.let {
            mainViewModel.paymentInfo.phoneDigits?.let { it1 -> updatePhoneUI(it, it1) }
        }
    }

    private fun observeExchangeRates() {
        mainViewModel.latestRatesLiveData.observe(this) {
            when (it) {
                is RequestResult.Error -> {
                    mainActivityBinding.loadingProgressBar.isVisible = false
                    mainActivityBinding.exchangeInfo.text = it.message
                }
                is RequestResult.Loading -> {
                    mainActivityBinding.loadingProgressBar.isVisible = true
                }
                is RequestResult.Success -> {
                    mainActivityBinding.loadingProgressBar.isVisible = false
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
                with(mainActivityBinding) {
                    payButton.isEnabled = true
                    fullNameContainer.isEnabled = true
                    countryContainer.isEnabled = true
                    phoneNumberContainer.isEnabled = true
                    amountContainer.isEnabled = true
                    noInternetLayout.visibility = View.GONE
                }

            } else {
                with(mainActivityBinding) {
                    payButton.isEnabled = false
                    fullNameContainer.isEnabled = false
                    countryContainer.isEnabled = false
                    phoneNumberContainer.isEnabled = false
                    amountContainer.isEnabled = false
                    exchangeInfo.text = ""
                    noInternetLayout.visibility = View.VISIBLE
                }
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
                updatePhoneUI(selectedCountry.phonePrefix, selectedCountry.phoneDigits)
                showExchangeRate(mainActivityBinding.transferAmountEditText.text.toString())
            }
        }
    }

    private fun updatePhoneUI(selectedCountryPrefix: String, selectedCountryPhoneDigits: Int) {
        mainActivityBinding.phoneNumberContainer.prefixText = selectedCountryPrefix
        mainActivityBinding.phoneNumberContainer.counterMaxLength =
            selectedCountryPhoneDigits
        mainActivityBinding.phoneNumberEditText.text?.clear()

        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = LengthFilter(selectedCountryPhoneDigits)
        mainActivityBinding.phoneNumberEditText.filters = fArray
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
            fullNameContainer.helperText = validName(fullNameEditText.text.toString())
            countryContainer.helperText = validCountry(countryACTextView.text.toString())
            phoneNumberContainer.helperText = validPhone(
                phoneNumberEditText.text.toString(),
                phoneNumberContainer.counterMaxLength
            )
            amountContainer.helperText = validAmount(transferAmountEditText.text.toString())
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
}