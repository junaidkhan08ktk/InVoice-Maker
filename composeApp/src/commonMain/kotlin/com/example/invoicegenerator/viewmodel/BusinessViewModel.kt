package com.example.invoicegenerator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicegenerator.data.dao.BusinessDao
import com.example.invoicegenerator.data.entity.Business
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BusinessViewModel(
    private val businessDao: BusinessDao
) : ViewModel() {

    private val _businessProfile = MutableStateFlow<Business?>(null)
    val businessProfile: StateFlow<Business?> = _businessProfile.asStateFlow()

    init {
        viewModelScope.launch {
            businessDao.getBusinessProfile().collect {
                _businessProfile.value = it
            }
        }
    }

    fun saveBusinessProfile(
        name: String,
        gstin: String,
        address: String,
        email: String?,
        phone: String?,
        defaultGstRate: Double
    ) {
        viewModelScope.launch {
            val business = Business(
                id = _businessProfile.value?.id ?: 0,
                name = name,
                gstin = gstin,
                address = address,
                email = email,
                phone = phone,
                defaultGstRate = defaultGstRate
            )
            businessDao.insertBusiness(business)
        }
    }

}
