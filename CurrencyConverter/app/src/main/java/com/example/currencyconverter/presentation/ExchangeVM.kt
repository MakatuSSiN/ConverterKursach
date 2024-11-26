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

        viewModelScope.launch {
            state = state.copy(
                allCurrencies = exchangeRepo.getAllCurrencies()

            )
        }
    }

    fun onAction(action: ExchangeAction) {
        when(action) {
            ExchangeAction.Clear -> {
                state = state.copy(
                    amount = "",
                    result = ""
                )
            }
            ExchangeAction.Delete -> {
                if(state.amount.isBlank()) return

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
                convert()
            }

            is ExchangeAction.SelectedTo -> {
                state = state.copy(
                    to = state.allCurrencies[action.index]
                )
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
}