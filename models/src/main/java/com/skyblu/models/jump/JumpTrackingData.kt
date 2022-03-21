package com.skyblu.models.jump

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import timber.log.Timber

//@Entity
data class JumpTrackingData(
    @PrimaryKey
    val jumpTrackingDataId : String,
    var walkingTrackingPoints : MutableList<TrackingPoint> = mutableListOf<TrackingPoint>(),
    var aircraftTrackingPoints : MutableList<TrackingPoint> = mutableListOf<TrackingPoint>(),
    var freefallTrackingPoints : MutableList<TrackingPoint> = mutableListOf<TrackingPoint>(),
    var canopyTrackingPoints : MutableList<TrackingPoint> = mutableListOf<TrackingPoint>(),
    var landedTrackingPoints : MutableList<TrackingPoint> = mutableListOf<TrackingPoint>(),
){
    fun allTrackingPoints(): List<TrackingPoint> {
        return walkingTrackingPoints + aircraftTrackingPoints + freefallTrackingPoints + canopyTrackingPoints + landedTrackingPoints
    }
    fun importantTrackingPoints(): List<TrackingPoint> {
        return walkingTrackingPoints + aircraftTrackingPoints + freefallTrackingPoints + canopyTrackingPoints + landedTrackingPoints
    }
    fun getMinPressure(list : List<TrackingPoint>) : Float {
        return list.minOf { it.airPressure }
    }
    fun getMinPressure() : Float {
        return allTrackingPoints().minOf { it.airPressure }
    }
    fun getMaxPressure(list : List<TrackingPoint>) : Float {
        return list.maxOf { it.airPressure }
    }
    fun getMaxPressure() : Float {
        return allTrackingPoints().maxOf { it.airPressure }
    }
    private fun getMinLatitude(list : List<TrackingPoint>) : Double {
        Timber.d("Size ${list.size}")
        return list.minOf { it.latitude }
    }
    private fun getMaxLatitude(list : List<TrackingPoint>) : Double {
        return list.maxOf { it.latitude }
    }
    private fun getMinLongitude(list : List<TrackingPoint>) : Double {
        return list.minOf { it.longitude }
    }
    private fun getMaxLongitude(list : List<TrackingPoint>) : Double {
        return list.maxOf { it.longitude }
    }
    fun getCenterPoint(list: List<TrackingPoint>) : LatLng{
        var latTotal = 0.0
        var lngTotal = 0.0
        for (i in list.indices){
            latTotal += list[i].latitude
            lngTotal += list[i].longitude
        }
        return LatLng(latTotal/list.size, lngTotal/list.size)
    }
    private fun getSouthwestCameraPoint() : LatLng{

        val minLat = getMinLatitude(importantTrackingPoints())
        val minLong = getMinLongitude(importantTrackingPoints())

        return LatLng(minLat, minLong)
    }
    private fun getNorthEastCameraPoint() : LatLng{
        val maxLat = getMaxLatitude(importantTrackingPoints())
        val maxLong = getMaxLongitude(importantTrackingPoints())

        return LatLng(maxLat, maxLong)
    }
    fun getCameraBounds() : LatLngBounds {
        return LatLngBounds(getSouthwestCameraPoint(), getNorthEastCameraPoint())
    }
    fun createLatLngList(list : List<TrackingPoint>) : List<LatLng>{
        val latLngList : MutableList<LatLng> = mutableListOf()
        for(i in list.indices){

            latLngList.add(LatLng(list[i].latitude, list[i].longitude))
        }
        return latLngList
    }
    fun getLastTrackingPoint() : TrackingPoint?{
        if(landedTrackingPoints.isNotEmpty()){
            return landedTrackingPoints.last()
        }
        if(canopyTrackingPoints.isNotEmpty()){
            return canopyTrackingPoints.last()
        }
        if(freefallTrackingPoints.isNotEmpty()){
            return freefallTrackingPoints.last()
        }
        if(aircraftTrackingPoints.isNotEmpty()){
            return aircraftTrackingPoints.last()
        }
        if(walkingTrackingPoints.isNotEmpty()){
            return walkingTrackingPoints.last()
        }
        return null
    }
    fun getFirstTrackingPoint() : TrackingPoint? {
        if (walkingTrackingPoints.isNotEmpty()) {
            return walkingTrackingPoints.first()
        } else {
            return null
        }
    }
}


