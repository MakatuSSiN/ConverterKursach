package com.example.currencyconverter.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.ConvertUser
import com.example.currencyconverter.domain.ExchangeRepo
import kotlinx.coroutines.launch
import com.example.currencyconverter.data.UserPreferences

class ExchangeVM(
    private val convertUser: ConvertUser,
    private val exchangeRepo: ExchangeRepo,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var state by mutableStateOf(ExchangeState())
        private set

    init {
        viewModelScope.launch {
            try {

                val userPreferencesFlow = userPreferences.userPreferencesFlow
                userPreferencesFlow.collect { (from, to) ->

                    val currencies = exchangeRepo.getAllCurrencies()
                    state = state.copy(allCurrencies = currencies)


                    val defaultFromCurrency = currencies.find { it.code == from } ?: currencies.firstOrNull()
                    val defaultToCurrency = currencies.find { it.code == to } ?: currencies.firstOrNull()

                    state = state.copy(
                        from = defaultFromCurrency ?: state.from,
                        to = defaultToCurrency ?: state.to,
                        amount = "1"
                    )

                    convert()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onAction(action: ExchangeAction) {
        when (action) {
            ExchangeAction.Clear -> {
                state = state.copy(
                    amount = "",
                    result = ""
                )
            }
            ExchangeAction.Delete -> {
                if (state.amount.isBlank()) return

                state = state.copy(
                    amount = state.amount.dropLast(1)
                )
                convert()
            }
            is ExchangeAction.Input -> {
                state = state.copy(
                    amount = state.amount + action.value
                )
                convert()
            }

            is ExchangeAction.SelectedFrom -> {
                state = state.copy(
                    from = state.allCurrencies[action.index]
                )
                savePreferences()
                convert()
            }

            is ExchangeAction.SelectedTo -> {
                state = state.copy(
                    to = state.allCurrencies[action.index]
                )
                savePreferences()
                convert()
            }
        }
    }

    private fun convert() {
        viewModelScope.launch {
            state = state.copy(
                result = convertUser(
                    fromCurrency = state.from.code,
                    toCurrency = state.to.code,
                    amount = state.amount
                )
            )
        }
    }

    private fun savePreferences() {
        viewModelScope.launch {
            userPreferences.saveUserPreferences(
                fromCurrency = state.from.code,
                toCurrency = state.to.code
            )
        }
    }
}
