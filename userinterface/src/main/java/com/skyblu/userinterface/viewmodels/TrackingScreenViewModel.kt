package com.skyblu.userinterface.viewmodels

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigator
import com.google.android.gms.maps.model.LatLng
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.ServerInterface
import com.skyblu.data.room.TrackingPointsDao
import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.models.jump.JumpTrackingData
import com.skyblu.models.jump.Skydive
import com.skyblu.models.jump.SkydiveDataPoint
import com.skyblu.models.jump.SkydiveWithDatapoints
import com.skyblu.userinterface.componants.generateStaticMapsUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.absoluteValue

enum class TrackingStatus(){
    NOT_TRACKING,
    TRACKING,
    TRACKING_COMPLETE
}

data class TrackingState(
    val jumpDropzone: MutableState<String> = mutableStateOf(""),
    val jumpTitle: MutableState<String> = mutableStateOf(""),
    val jumpNumber: MutableState<String> = mutableStateOf(""),
    val jumpAircraft: MutableState<String> = mutableStateOf(""),
    val jumpEquipment: MutableState<String> = mutableStateOf(""),
    val jumpDescription: MutableState<String> = mutableStateOf(""),
    val staticMap : MutableState<String> = mutableStateOf(""),
    val allPoints : MutableState<MutableList<SkydiveDataPoint>> = mutableStateOf(mutableStateListOf()),
    val trackingStatus: MutableState<TrackingStatus> = mutableStateOf(TrackingStatus.NOT_TRACKING)
)


@HiltViewModel
class TrackingScreenViewModel @Inject constructor(
    private val roomInterface : TrackingPointsDao,
    @Named("AppContext") private val context : Context,
    private val clientToService: ClientToService,
    private val server : ServerInterface,
    private val authenticationInterface: AuthenticationInterface
) : ViewModel() {



    val state by mutableStateOf(TrackingState())
    val trackingStatus: MutableState<TrackingStatus> = mutableStateOf(TrackingStatus.NOT_TRACKING)

    init {
        clientToService.setOnRecieveSkydiveDataPoint { receiveTrackingPoint(it) }
    }

    private fun receiveTrackingPoint(trackingPoint: SkydiveDataPoint) {
        Timber.d("Adding Point VS = ${trackingPoint.verticalSpeed}")
        if(trackingPoint.verticalSpeed.absoluteValue < 100){
            Timber.d("Also Adding Point")
            state.allPoints.value.add(trackingPoint)
        }
    }

    fun onStartTrackingClicked() {
        trackingStatus.value = TrackingStatus.TRACKING
        clientToService.startTrackingService()
    }
    fun onStopTrackingService() {
        trackingStatus.value = TrackingStatus.TRACKING_COMPLETE
        clientToService.stopTrackingService()
    }

    private fun createJump() : Skydive{
        return Skydive(
            title = state.jumpTitle.toString(),
            skydiveID = state.allPoints.value[0].skydiveID,
            aircraft = state.jumpAircraft.value,
            equipment = state.jumpEquipment.value,
            description = state.jumpDescription.value,
            skydiverID = authenticationInterface.getCurrentUser()!!,
            staticMapUrl = generateStaticMapsUrl(context = context, state.allPoints.value),
            dropzone = state.jumpDropzone.value,
            date = System.currentTimeMillis()
        )
    }

    fun updateStaticMap(){
        state.staticMap.value = generateStaticMapsUrl(context = context, state.allPoints.value)
    }



    fun queueJump(){
        viewModelScope.launch {
            server.uploadSkydiveWithDatapoints(skydiveWithDataPoints = SkydiveWithDatapoints(createJump(), state.allPoints.value), context)
        }


    }

    fun stopTracking(){
        clientToService.stopTrackingService()
    }


}




