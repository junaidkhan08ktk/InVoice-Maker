package com.example.invoicegenerator.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    private val IS_PRO_KEY = booleanPreferencesKey("is_pro")

    val isPro: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_PRO_KEY] ?: false
    }

    suspend fun setProStatus(isPro: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PRO_KEY] = isPro
        }
    }
}
