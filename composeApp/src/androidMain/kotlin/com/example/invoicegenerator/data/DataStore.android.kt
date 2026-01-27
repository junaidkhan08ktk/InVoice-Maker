package com.example.invoicegenerator.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

fun createAndroidDataStore(context: Context): DataStore<Preferences> {
    return createDataStore {
        context.preferencesDataStoreFile(DATASTORE_FILE_NAME).absolutePath
    }
}
