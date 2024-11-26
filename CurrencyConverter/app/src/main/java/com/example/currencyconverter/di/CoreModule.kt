package com.example.currencyconverter.di

import com.example.currencyconverter.data.ExchangeRepoImpl
import com.example.currencyconverter.data.UserPreferences
import com.example.currencyconverter.domain.ConvertUser
import com.example.currencyconverter.domain.ExchangeRepo
import com.example.currencyconverter.presentation.ExchangeState
import com.example.currencyconverter.presentation.ExchangeVM
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val coreModule = module {

    singleOf(::ExchangeRepoImpl).bind<ExchangeRepo>()

    single { ConvertUser(get()) }
    single { UserPreferences (context = get()) }
    viewModel {ExchangeVM(get(), get(), get())}
    single {
        HttpClient(CIO) {
            expectSuccess = true

            engine {
                endpoint {
                    keepAliveTime = 5000
                    connectTimeout = 5000
                    connectAttempts = 3
                }
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}