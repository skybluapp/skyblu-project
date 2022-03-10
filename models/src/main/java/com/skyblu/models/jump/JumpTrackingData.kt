package com.skyblu.models.jump

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class JumpTrackingData(
    var walkingTrackingPoints : List<TrackingPoint> = listOf(),
    var aircraftTrackingPoints : List<TrackingPoint> = listOf(),
    var freefallTrackingPoints : List<TrackingPoint> = listOf(),
    var canopyTrackingPoints : List<TrackingPoint> = listOf(),
    var landedTrackingPoints : List<TrackingPoint> = listOf(),
    var exitAltitude : Int = 14000,
    var openAltitude : Int = 4000,
    var freefallTime : Int = 60,
    var canopyTime : Int = 240,
    var freefallMaxVerticalSpeed: Int = 100,
    var freefallMaxHorizontalSpeed: Int = 20,
    var canopyMaxVerticalSpeed: Int = 10,
    var canopyMaxHorizontalSpeed: Int = 20,
){
    val allTrackingPoints = walkingTrackingPoints + aircraftTrackingPoints + freefallTrackingPoints + canopyTrackingPoints + landedTrackingPoints
    val importantTrackingPoints = freefallTrackingPoints + canopyTrackingPoints

    fun getMinPressure(list : List<TrackingPoint>) : Float {
        return list.minOf { it.airPressure }
    }
    fun getMinPressure() : Float {
        return allTrackingPoints.minOf { it.airPressure }
    }
    fun getMaxPressure(list : List<TrackingPoint>) : Float {
        return list.maxOf { it.airPressure }
    }
    fun getMaxPressure() : Float {
        return allTrackingPoints.maxOf { it.airPressure }
    }



    fun getMinLatitude(list : List<TrackingPoint>) : Double {
        return list.minOf { it.location.latitude }
    }

    fun getMaxLatitude(list : List<TrackingPoint>) : Double {
        return list.maxOf { it.location.latitude }
    }

    fun getMinLongitude(list : List<TrackingPoint>) : Double {
        return list.minOf { it.location.longitude }
    }

    fun getMaxLongitude(list : List<TrackingPoint>) : Double {
        return list.maxOf { it.location.longitude }
    }


    fun getCenterPoint(list: List<TrackingPoint>) : LatLng{
        var latTotal = 0.0
        var lngTotal = 0.0
        for (i in list.indices){
            latTotal += list[i].location.latitude
            lngTotal += list[i].location.longitude
        }
        return LatLng(latTotal/list.size, lngTotal/list.size)
    }

    fun getSouthwestCameraPoint() : LatLng{
        val minLat = getMinLatitude(importantTrackingPoints)
        val minLong = getMinLongitude(importantTrackingPoints)

        return LatLng(minLat, minLong)
    }

    fun getNorthEastCameraPoint() : LatLng{
        val maxLat = getMaxLatitude(importantTrackingPoints)
        val maxLong = getMaxLongitude(importantTrackingPoints)

        return LatLng(maxLat, maxLong)
    }

    fun getCameraBounds() : LatLngBounds {
        return LatLngBounds(getSouthwestCameraPoint(), getNorthEastCameraPoint())
    }

    fun createLatLngList(list : List<TrackingPoint>) : List<LatLng>{
        var latLngList : MutableList<LatLng> = mutableListOf()
        for(i in list.indices){
            latLngList.add(list[i].location)
        }
        return latLngList
    }



}
fun LatLng.stringConvert() : String{
    return "${latitude},${longitude}"
}