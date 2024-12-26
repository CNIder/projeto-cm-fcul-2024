package com.example.projeto_cm_24_25.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepository(private val context: Context) {
    companion object {
        private val Context.datastore : DataStore<Preferences> by preferencesDataStore("user")
        // Chave para guardar o nome do utilizador
        val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    suspend fun saveUserName(userName: String) {
        context.datastore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }

    val getUserName: Flow<String?> = context.datastore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: ""
    }
}