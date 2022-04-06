package com.skyblu.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    val readWriteDatastore: ReadWriteDatastore
) :

    DatastoreInterface{
    override suspend fun readAircraftCertaintyKey(): Int {
//        var aircraftCertainty = 5
//        readWriteDatastore.read(AIRCRAFT_CERTAINTY_KEY, 5).collect { aircraftCertainty = it }
//        return aircraftCertainty
        return 5
    }
}







fun <T> ViewModel.readFromDatastore(
    mutable: MutableStateFlow<RequestState<T>>,
    key: Preferences.Key<String>,
    default: String,
    mapping: (String) -> T,
    dataStoreRepository: ReadWriteDatastore
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
    dataStoreRepository: ReadWriteDatastore,
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
    dataStoreRepository: ReadWriteDatastore
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
    dataStoreRepository: ReadWriteDatastore
) {
    viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.write(
            int,
            key
        )
    }
}