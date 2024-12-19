package com.example.currencyconverter.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.currencyconverter.domain.ConvertUser
import com.example.currencyconverter.domain.ExchangeRepo
import kotlinx.coroutines.launch
import com.example.currencyconverter.data.UserPreferences
import org.w3c.dom.NameList

class ExchangeVM(
    private val convertUser: ConvertUser,
    private val exchangeRepo: ExchangeRepo,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var state by mutableStateOf(ExchangeState())
        private set

    var sch: Int = 1
    var sch1: Int = 0
    var NalToch: Boolean = false

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
                sch = 0
                NalToch = false
            }

            ExchangeAction.Delete -> {
                if (sch > 0 && NalToch == false) sch -= 1
                if (state.amount.takeLast(1) == ".") {
                    NalToch = false
                }
                else if (NalToch == true) {
                    sch1 -= 1
                }


                if (state.amount.isBlank()) return
                state = state.copy(
                    amount = state.amount.dropLast(1)
                )
                convert()
            }

            is ExchangeAction.Input -> {
                if (action.value == ".") {
                    if (NalToch == false) {
                        sch1 = 0
                        NalToch = true
                        state = state.copy(
                                amount = state.amount + action.value
                            )
                        convert()
                    }

                }
                else if (action.value == "00") {
                    if (NalToch == true && sch1 == 1) {
                        action.value = "0"
                        sch1 += 1
                        state = state.copy(
                            amount = state.amount + action.value
                        )
                        convert()

                    }
                    else if (NalToch == false && sch == 6) {
                        action.value = "0"
                        sch += 1
                        state = state.copy(
                            amount = state.amount + action.value
                        )
                        convert()
                    }
                    else if (NalToch == false && sch < 6) {
                        sch += 2
                        state = state.copy(
                            amount = state.amount + action.value
                        )
                        convert()
                    }
                    else if (NalToch == true && sch1 == 0) {
                        sch1 += 2
                        state = state.copy(
                            amount = state.amount + action.value
                        )
                        convert()
                    }


                }
                else if (NalToch == true && sch1 < 2) {
                    sch1 += 1
                    state = state.copy(
                        amount = state.amount + action.value
                    )
                    convert()
                }
                else if (NalToch == false && sch < 7) {
                        state = state.copy(
                            amount = state.amount + action.value
                        )
                        convert()
                        sch += 1
                    }



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
