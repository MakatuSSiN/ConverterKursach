package com.example.currencyconverter.data
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(context: Context) {

    private val dataStore = context.dataStore


    companion object {
        val FROM_CURRENCY_KEY = stringPreferencesKey("from_currency")
        val TO_CURRENCY_KEY = stringPreferencesKey("to_currency")
    }
    val userPreferencesFlow: Flow<Pair<String, String>> = dataStore.data.map { preferences ->
        val fromCurrency = preferences[FROM_CURRENCY_KEY] ?: "USD"
        val toCurrency = preferences[TO_CURRENCY_KEY] ?: "RUB"
        fromCurrency to toCurrency
    }

    suspend fun saveUserPreferences(fromCurrency: String, toCurrency: String) {
        dataStore.edit { preferences ->
            preferences[FROM_CURRENCY_KEY] = fromCurrency
            preferences[TO_CURRENCY_KEY] = toCurrency
        }
    }
}
