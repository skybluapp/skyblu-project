package com.skyblu.userinterface.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.datastore.PreferenceKeys
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.room.TrackingPointsDao
import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.JumpWithDatapoints
import com.skyblu.models.jump.SkydiveDataPoint
import com.skyblu.userinterface.componants.generateStaticMapsUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.absoluteValue

enum class TrackingStatus() {
    NOT_TRACKING,
    TRACKING,
    TRACKING_COMPLETE
}

data class TrackingState(
    val dropzone: MutableState<String> = mutableStateOf(""),
    val title: MutableState<String> = mutableStateOf(""),
    val jumpNumber: MutableState<Int> = mutableStateOf(0),
    val aircraft: MutableState<String> = mutableStateOf(""),
    val equipment: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf(""),
    val staticMap: MutableState<String> = mutableStateOf(""),
    val allPoints: MutableState<MutableList<SkydiveDataPoint>> = mutableStateOf(mutableStateListOf()),
    val trackingStatus: MutableState<TrackingStatus> = mutableStateOf(TrackingStatus.NOT_TRACKING)
)

@HiltViewModel
class TrackingScreenViewModel @Inject constructor(
    private val roomInterface: TrackingPointsDao,
    @Named("AppContext") private val context: Context,
    private val clientToService: ClientToService,
    val readServer: ReadServerInterface,
    val writeServer: WriteServerInterface,
    private val authenticationInterface: AuthenticationInterface,
    private val datastore: DatastoreInterface
) : ViewModel() {

    val state by mutableStateOf(TrackingState())
    val trackingStatus: MutableState<TrackingStatus> = mutableStateOf(TrackingStatus.NOT_TRACKING)

    init {
        clientToService.setOnRecieveSkydiveDataPoint { receiveTrackingPoint(it) }
        datastore.readStringFromDatastore(PreferenceKeys.LAST_DROPZONE, this){
            state.dropzone.value = it
        }
        datastore.readStringFromDatastore(PreferenceKeys.LAST_AIRCRAFT, this){
            state.aircraft.value = it
        }
        datastore.readIntFromDatastore(PreferenceKeys.LAST_JUMP_NUMBER, this){
            state.jumpNumber.value = it + 1
        }
        datastore.readStringFromDatastore(PreferenceKeys.LAST_EQUIPMENT, this){
            state.equipment.value = it
        }

    }

    private fun receiveTrackingPoint(trackingPoint: SkydiveDataPoint) {
        Timber.d("Adding Point VS = ${trackingPoint.verticalSpeed}")
        if (trackingPoint.verticalSpeed.absoluteValue < 100) {
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

    private fun createJump(): Jump {
        return Jump(
            title = state.title.value.toString(),
            jumpID = state.allPoints.value[0].jumpID,
            aircraft = state.aircraft.value,
            equipment = state.equipment.value,
            description = state.description.value,
            userID = authenticationInterface.getCurrentUser()!!,
            staticMapUrl = generateStaticMapsUrl(
                context = context,
                state.allPoints.value
            ),
            dropzone = state.dropzone.value,
            date = System.currentTimeMillis()
        )
    }

    fun updateStaticMap() {
        state.staticMap.value = generateStaticMapsUrl(
            context = context,
            state.allPoints.value
        )
    }

    fun queueJump() {
        datastore.writeIntToDataStore(
            data = state.jumpNumber.value,
            key = PreferenceKeys.LAST_JUMP_NUMBER,
            viewModel = this
        )
        datastore.writeStringToDatastore(
            data = state.aircraft.value,
            key = PreferenceKeys.LAST_AIRCRAFT,
            viewModel = this
        )
        datastore.writeStringToDatastore(
            data = state.equipment.value,
            key = PreferenceKeys.LAST_EQUIPMENT,
            viewModel = this
        )
        datastore.writeStringToDatastore(
            data = state.dropzone.value,
            key = PreferenceKeys.LAST_DROPZONE,
            viewModel = this
        )
        viewModelScope.launch {
            writeServer.uploadJump(
                jumpWithDatapoints = JumpWithDatapoints(
                    createJump(),
                    state.allPoints.value
                ),
                context
            )
        }

    }

    fun stopTracking() {
        clientToService.stopTrackingService()
    }

}





