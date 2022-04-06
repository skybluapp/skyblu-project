package com.skyblu.datastore

import android.content.Context
import android.renderscript.RenderScript
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES)



class DataStoreRepository(
    private val context: Context
) {

    private val dataStore = context.dataStore

    fun read(key: Preferences.Key<String>, default: String = "DefaultString"): Flow<String> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val result = preferences[key] ?: default
            result
        }
    }

    fun read(key: Preferences.Key<Int>, default: Int = 0): Flow<Int> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val result = preferences[key] ?: default
            result
        }
    }

    suspend fun write(data: String , key : Preferences.Key<String>)  {
        dataStore.edit { preferences ->
            preferences[key] = data
        }
    }

    suspend fun write(data: Int , key : Preferences.Key<Int>)  {
        dataStore.edit { preferences ->
            preferences[key] = data
        }
    }
}


sealed class RequestState<out T>{
    object Idle : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data : T) : RequestState<T>()
    data class Error(val error : Throwable) : RequestState<Nothing>()
}