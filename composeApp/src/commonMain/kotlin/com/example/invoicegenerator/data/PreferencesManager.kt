package com.example.invoicegenerator.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    private val IS_PRO_KEY = booleanPreferencesKey("is_pro")
    private val LANGUAGE_KEY = stringPreferencesKey("language")
    private val CURRENCY_KEY = stringPreferencesKey("currency")

    val isPro: Flow<Boolean> = dataStore.data.map { preferences ->
        //preferences[IS_PRO_KEY] ?: false
        preferences[IS_PRO_KEY] ?: true
    }

    val language: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]
    }

    val currency: Flow<String> = dataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: "USD"
    }

    suspend fun setProStatus(isPro: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PRO_KEY] = isPro
        }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun setCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }
}
