package com.skyblu.userinterface.viewmodels

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.skyblu.models.jump.JumpTrackingData
import com.skyblu.models.jump.TrackingPoint
import timber.log.Timber
import kotlin.math.pow

const val AIR_PRESSURE_THRESHOLD = 0.5f
const val ALTITUDE_THRESHOLD = 2f
const val LOCATION_THRESHOLD = 5f

enum class AltitudeStatus(val label: String) {
    ALTITUDE_INCREASING(label = "Altitude Increasing"),
    ALTITUDE_DECREASING(label = "Altitude Decreasing"),
    ALTITUDE_STEADY(label = "Altitude Steady"),
}

enum class JumpStatus(val label : String){
    WALKING(label = "Walking"),
    AIRCRAFT(label = "In Aircraft"),
    FREEFALL("In Freefall"),
    CANOPY("Under Canopy"),
    LANDED("Landed"),
}

class TrackingScreenViewModel : ViewModel() {

    val pointsAccepted: MutableState<Int> = mutableStateOf(0)
    val pointsRejected: MutableState<Int> = mutableStateOf(0)
    val pointsTotal: MutableState<Int> = mutableStateOf(0)
    val groundAirPressure : MutableState<Float> = mutableStateOf(0F)
    val baseAltitude : MutableState<Float> = mutableStateOf(0F)
    val altitude : MutableState<Float> = mutableStateOf(0F)
    val basePressure : MutableState<Float> = mutableStateOf(0F)
    val pressure : MutableState<Float> = mutableStateOf(0F)
    val totalDistance : MutableState<Float> = mutableStateOf(0F)
    val sectorDistance : MutableState<Float> = mutableStateOf(0F)

    val altitudeStatus: MutableState<AltitudeStatus> =
        mutableStateOf(AltitudeStatus.ALTITUDE_STEADY)
    val trackingData = JumpTrackingData("10")
    val jumpStatus: MutableState<JumpStatus> = mutableStateOf(JumpStatus.WALKING)

    var lastPressureEntry : Float? = null
    var lastAltitudeEntry : Float? = null
    var lastLocationEntry : LatLng? = null
    var lastLatitudeEntry : Double? = null
    var lastlongitudeEntry : Double? = null



    fun receiveTrackingPoint(trackingPoint: TrackingPoint) {

        // Increase total location point tally
        pointsTotal.value++
        var accept = false

        //Get values for start and end air pressure and start and end LatLng
        val endLatitude = trackingPoint.latitude
        val endLongitude = trackingPoint.longitude
        val endPressure = trackingPoint.airPressure
        val endAltitude = trackingPoint.altitude

        val startLatitude : Double = lastLatitudeEntry ?: trackingPoint.latitude
        val startLongitude : Double = lastlongitudeEntry ?: trackingPoint.longitude
        //val startLatLng : LatLng = lastLocationEntry ?: trackingPoint.location
        val startPressure : Float = lastPressureEntry ?: trackingPoint.airPressure
        val startAltitude : Float = lastAltitudeEntry ?: trackingPoint.altitude



        //Calculate Pressure Difference and Distance Travelled
        val altitudeDifference = endAltitude - startAltitude // Positive if altitude increasing
        val pressureDifference = startPressure - endPressure
        val distanceResults = floatArrayOf(0F)
        //Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, endLatLng.latitude, endLatLng.longitude, distanceResults)
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceResults)
        val distance = distanceResults[0]

        //Accept TrackingPoint if it is the first value and calibrate ground level air pressure
        if(pointsTotal.value == 1){
            accept = true
            lastPressureEntry = trackingPoint.airPressure
            //lastLocationEntry = trackingPoint.location
            lastLatitudeEntry = trackingPoint.latitude
            lastLatitudeEntry = trackingPoint.longitude
            lastAltitudeEntry = trackingPoint.altitude
            groundAirPressure.value = trackingPoint.airPressure
            baseAltitude.value = trackingPoint.altitude
            basePressure.value = trackingPoint.airPressure
        }

        //Accept Tracking Point if Air Pressure has changed significantly
//        if(pressureDifference > AIR_PRESSURE_THRESHOLD || pressureDifference < 0 - AIR_PRESSURE_THRESHOLD){
//            lastPressureEntry = trackingPoint.airPressure
//            accept = true
//        }


        if(altitudeDifference > ALTITUDE_THRESHOLD || altitudeDifference < 0 - ALTITUDE_THRESHOLD){
            lastPressureEntry = trackingPoint.airPressure
            lastAltitudeEntry = trackingPoint.altitude
            accept = true
        }

        //Accept Tracking Point if Location has changed significantly
        if( distance > LOCATION_THRESHOLD){
            //lastLocationEntry = trackingPoint.location
            lastLatitudeEntry = trackingPoint.latitude
            lastlongitudeEntry = trackingPoint.longitude
            totalDistance.value = totalDistance.value + distance
            sectorDistance.value = sectorDistance.value + distance
            accept = true
        }

        //Accept Tracking Point if previous tracking point had a significant change in altitude
        if( altitudeStatus.value == AltitudeStatus.ALTITUDE_DECREASING || altitudeStatus.value == AltitudeStatus.ALTITUDE_INCREASING){
            accept = true
        }



        //Accept or reject the new trackingPoint
        if (accept) {

            //Increment Points Accepted
            pointsAccepted.value++
            altitude.value = trackingPoint.altitude
            pressure.value = trackingPoint.airPressure



            when {
                altitudeDifference > ALTITUDE_THRESHOLD -> {
                    Timber.d("Altitude Increasing")
                    altitudeStatus.value = AltitudeStatus.ALTITUDE_INCREASING
                    if(jumpStatus.value == JumpStatus.WALKING){
                        trackingData.aircraftTrackingPoints.add(trackingData.walkingTrackingPoints.last())
                        jumpStatus.value = JumpStatus.AIRCRAFT
                        sectorDistance.value = 0F
                    }
                }
                altitudeDifference < 0 - ALTITUDE_THRESHOLD -> {
                    Timber.d("Altitude Decreasing")
                    altitudeStatus.value = AltitudeStatus.ALTITUDE_DECREASING
                    if(jumpStatus.value == JumpStatus.AIRCRAFT){
                        trackingData.freefallTrackingPoints.add(trackingData.aircraftTrackingPoints.last())
                        jumpStatus.value = JumpStatus.FREEFALL
                        sectorDistance.value = 0F
                    }
                }
                else -> {
                    altitudeStatus.value = AltitudeStatus.ALTITUDE_STEADY
                }
            }

            when(jumpStatus.value){
                JumpStatus.WALKING -> trackingData.walkingTrackingPoints.add(trackingPoint)
                JumpStatus.AIRCRAFT -> trackingData.aircraftTrackingPoints.add(trackingPoint)
                JumpStatus.FREEFALL -> trackingData.freefallTrackingPoints.add(trackingPoint)
                JumpStatus.CANOPY -> trackingData.canopyTrackingPoints.add(trackingPoint)
                JumpStatus.LANDED -> trackingData.landedTrackingPoints.add(trackingPoint)
                else -> {}
            }


        } else {
            pointsRejected.value++
        }
    }
}