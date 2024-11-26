package com.example.currencyconverter.domain

interface ExchangeRepo {

    suspend fun convert(
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): Double

    suspend fun getAllCurrencies(): List<Currency>

}