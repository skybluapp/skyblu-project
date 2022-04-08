package com.skyblu.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DatastoreInterface{
    fun readIntFromDatastore(
        key: Preferences.Key<Int>,
        viewModel: ViewModel,
        defaultValue : Int = 0,
        onRead : (Int) -> Unit
    )
    fun writeIntToDataStore(
        key : Preferences.Key<Int>,
        data : Int,
        viewModel: ViewModel,
    )
    fun readStringFromDatastore(
        key: Preferences.Key<String>,
        viewModel: ViewModel,
        defaultValue : Int = 0,
        onRead : (String) -> Unit
    )
    fun writeStringToDatastore(
        key : Preferences.Key<String>,
        data : String,
        viewModel: ViewModel,
    )
}

class DataStoreRepository @Inject constructor(
    private val readWriteDatastore: ReadWriteDatastore
) : DatastoreInterface {


    override fun readIntFromDatastore(
        key: Preferences.Key<Int>,
        viewModel: ViewModel,
        defaultValue : Int,
        onRead : (Int) -> Unit
    ) {
        viewModel.viewModelScope.launch {
            readWriteDatastore.read(
                key,
                0
            ).collect {
                onRead(it)
            }
        }
    }

    override fun writeIntToDataStore(
        key : Preferences.Key<Int>,
        data : Int,
        viewModel: ViewModel,
    ){
        viewModel.viewModelScope.launch {
            readWriteDatastore.write(data, key)
        }

    }

    override fun readStringFromDatastore(
        key: Preferences.Key<String>,
        viewModel: ViewModel,
        defaultValue : Int,
        onRead : (String) -> Unit
    ) {
        viewModel.viewModelScope.launch {
            readWriteDatastore.read(
                key,
                ""
            ).collect {
                onRead(it)
            }
        }
    }

    override fun writeStringToDatastore(
        key : Preferences.Key<String>,
        data : String,
        viewModel: ViewModel,
    ){
        viewModel.viewModelScope.launch {
            readWriteDatastore.write(data, key)
        }

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



