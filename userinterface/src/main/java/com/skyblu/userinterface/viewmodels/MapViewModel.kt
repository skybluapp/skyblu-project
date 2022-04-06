package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.ServerInterface
import com.skyblu.models.jump.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class MapViewModelState(
    val isLoading: MutableState<Boolean> = mutableStateOf(true),
    val isDatapointsEmpty : MutableState<Boolean> = mutableStateOf(true),
    val datapoints: MutableList<SkydiveDataPoint> = mutableListOf()
)

@OptIn(InternalCoroutinesApi::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    val authentication: AuthenticationInterface,
    val server: ServerInterface
) : ViewModel() {

    val state by mutableStateOf(MapViewModelState())

    fun setJump(skydiveID: String) {
        state.isLoading.value = true
        viewModelScope.launch {
            val querySnapshot : QuerySnapshot? = server.getDatapointsFromServer(skydiveID = skydiveID).getOrNull()
            querySnapshot?.documents?.forEach{ document ->
                state.datapoints.add(document.toDatapoint())
            }
            state.isLoading.value = false

        }
        viewModelScope.launch {
            server.getSkydiverFromServer(skydiveID)
        }
    }
}

fun DocumentSnapshot.toDatapoint() : SkydiveDataPoint{
    Timber.d("DATAPOINT ID" + this[DatapointParameters.DATAPOINT_ID].toString())
    return SkydiveDataPoint(
        dataPointID = this[DatapointParameters.DATAPOINT_ID].toString(),
        skydiveID = this[DatapointParameters.SKYDIVE_ID].toString(),
        latitude = this[DatapointParameters.LATITUDE].toString().toDouble(),
        longitude = this[DatapointParameters.LONGITUDE].toString().toDouble(),
        airPressure = this[DatapointParameters.AIR_PRESSURE].toString().toFloat(),
        altitude = this[DatapointParameters.ALTITUDE].toString().toFloat(),
        timeStamp = this[DatapointParameters.TIMESTAMP].toString().toLong(),
        verticalSpeed =this[DatapointParameters.VERTICAL_SPEED].toString().toFloat() ,
        groundSpeed = this[DatapointParameters.GROUND_SPEED].toString().toFloat(),
        phase = SkydivePhase.valueOf(this[DatapointParameters.PHASE].toString()) ,
    )
}