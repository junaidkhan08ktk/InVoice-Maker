package com.example.invoicegenerator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicegenerator.data.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val language: Flow<String?> = preferencesManager.language
    val currency: Flow<String> = preferencesManager.currency

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            preferencesManager.setCurrency(currency)
        }
    }
}
