package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.skyblu.data.users.SavedSkydivesInterface
import javax.inject.Inject
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DataStoreRepository
import com.skyblu.data.datastore.PreferenceKeys
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Named

data class EditState(
    val title : MutableState<String> = mutableStateOf(""),
    val equipment : MutableState<String> = mutableStateOf(""),
    val dropzone : MutableState<String> = mutableStateOf(""),
    var aircraft : MutableState<String> = mutableStateOf(""),
    val description : MutableState<String> = mutableStateOf(""),
    val jumpNumber : MutableState<Int> = mutableStateOf(0),
    var jumpID : String = ""
)

@HiltViewModel
class EditViewModel @Inject constructor(
    @Named("AppContext") val applicationContext : Context,
    private val savedSkydives : SavedSkydivesInterface,
    val readServer : ReadServerInterface,
    val writeServer : WriteServerInterface,
    val authentication : AuthenticationInterface,
    val dataStore : DataStoreRepository
) : ViewModel(){

    val state by mutableStateOf(EditState())

    fun updateSkydive(){
        writeServer.updateJump(
            jumpID = state.jumpID,
            equipment = state.equipment.value,
            dropzone = state.dropzone.value,
            aircraft = state.aircraft.value,
            description = state.description.value,
            title = state.title.value,
            applicationContext = applicationContext,
            jumpNumber = state.jumpNumber.value,
        )
        dataStore.writeIntToDataStore(data = state.jumpNumber.value, key = PreferenceKeys.LAST_JUMP_NUMBER, viewModel = this)
        dataStore.writeStringToDatastore(data = state.aircraft.value, key = PreferenceKeys.LAST_AIRCRAFT, viewModel = this)
        dataStore.writeStringToDatastore(data = state.equipment.value, key = PreferenceKeys.LAST_EQUIPMENT, viewModel = this)
        dataStore.writeStringToDatastore(data = state.dropzone.value, key = PreferenceKeys.LAST_DROPZONE, viewModel = this)
    }

    init{
        val skydive = savedSkydives.skydive
        if (skydive != null) {
            state.title.value = skydive.title
            state.equipment.value = skydive.equipment
            state.dropzone.value = skydive.dropzone
            state.description.value = skydive.description
            state.jumpID = skydive.jumpID
            state.aircraft.value = skydive.aircraft
            state.jumpNumber.value = skydive.jumpNumber
        }
        dataStore.readIntFromDatastore(key = PreferenceKeys.LAST_JUMP_NUMBER, viewModel = this){ lastJumpNumber ->
            state.jumpNumber.value = lastJumpNumber
        }

    }

}
