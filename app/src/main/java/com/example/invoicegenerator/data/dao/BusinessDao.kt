package com.example.invoicegenerator.data.dao

import androidx.room.*
import com.example.invoicegenerator.data.entity.Business
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    @Query("SELECT * FROM business_profile LIMIT 1")
    fun getBusinessProfile(): Flow<Business?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: Business)

    @Update
    suspend fun updateBusiness(business: Business)
}
