package com.example.currencyconverter.domain

class ConvertUser (
    private val exchangeRepo : ExchangeRepo
) {
    suspend operator fun invoke(

        fromCurrency: String,
        toCurrency: String,
        amount: String

    ): String {

        if (fromCurrency.isBlank()) return ""
        if (toCurrency.isBlank()) return ""
        if (amount.isBlank()) return ""

        if (fromCurrency == toCurrency) return amount

        val amountDouble = amount.toDoubleOrNull() ?: return ""

        val result = exchangeRepo.convert(
            fromCurrency, toCurrency, amountDouble
        )

        return result.toString()
    }
}