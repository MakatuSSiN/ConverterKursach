package com.example.currencyconverter.data

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeDto(
    val conversion_result: Double
)
