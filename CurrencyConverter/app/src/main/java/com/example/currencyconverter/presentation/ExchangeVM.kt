package com.example.currencyconverter.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.ConvertUser
import com.example.currencyconverter.domain.ExchangeRepo
import kotlinx.coroutines.launch

class ExchangeVM(
    private val convertUser: ConvertUser,
    private val exchangeRepo: ExchangeRepo
): ViewModel() {

    var state by mutableStateOf(ExchangeState())
        private set

    init {
        convert()
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
}