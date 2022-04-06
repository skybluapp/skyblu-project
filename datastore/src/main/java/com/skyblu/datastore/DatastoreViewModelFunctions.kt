package com.skyblu.datastore

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect


fun <T> ViewModel.readFromDatastore(
    mutable: MutableStateFlow<RequestState<T>>,
    key: Preferences.Key<String>,
    default: String,
    mapping: (String) -> T,
    dataStoreRepository: DataStoreRepository
) {
    mutable.value = RequestState.Loading
    try {
        viewModelScope.launch {
            dataStoreRepository.read(
                key,
                default
            )
                .map { mapping(it) }
                .collect {
                    mutable.value = RequestState.Success(it)
                }
        }
    } catch (e: Exception) {
        mutable.value = RequestState.Error(e)
    }
}


fun <T> ViewModel.readFromDatastore(
    mutable: MutableStateFlow<RequestState<T>>,
    key: Preferences.Key<Int>,
    default: Int,
    mapping: (Int) -> T,
    dataStoreRepository: DataStoreRepository,
    onLoad: (T) -> Unit
) {
    mutable.value = RequestState.Loading
    try {
        viewModelScope.launch {
            dataStoreRepository.read(
                key,
                default
            )
                .map { mapping(it) }
                .collect {
                    mutable.value = RequestState.Success(it)
                    onLoad(it)
                }
        }
    } catch (e: Exception) {
        mutable.value = RequestState.Error(e)
    }
}

fun ViewModel.writeToDatastore(
    key: Preferences.Key<String>,
    string: String,
    dataStoreRepository: DataStoreRepository
) {
    viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.write(
            string,
            key
        )
    }
}

fun ViewModel.writeToDatastore(
    key: Preferences.Key<Int>,
    int: Int,
    dataStoreRepository: DataStoreRepository
) {
    viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.write(
            int,
            key
        )
    }
}