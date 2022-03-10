package com.skyblu.models.jump

import android.location.Location
import android.util.Log

object JumpCalculators {

    fun calculateHorizontalDistance(points : List<Location>) : Float{
        val numberOfPoints = points.size
        var totalLength = 0f
        for(i in 0 until numberOfPoints - 1){
            totalLength += calculateSingleDistance(points[i], points[i+1])
        }
        return totalLength
    }

    fun calculateMaxHorizontalSpeed(points: List<Location>) : Float {
        val numberOfPoints = points.size
        var highestSpeed = 0F
        for(i in 0 until numberOfPoints - 1){
            Log.d("EASY", points[i].speed.toString())
            if(points[i].speed > highestSpeed){
                highestSpeed = points[i].speed
            }
        }
        return highestSpeed
    }

    private fun calculateSingleDistance(locationOne : Location, locationTwo : Location) : Float{
        return locationOne.distanceTo(locationTwo)
    }

    fun createLocation(latitude : Double, longitude : Double, altitude : Double, horizontalSpeed : Float = 0F, accuracy : Float = 1F) : Location{
        val PROVIDER = "flp"
        val location = Location(PROVIDER)
        location.latitude = latitude
        location.longitude = longitude
        location.altitude = altitude
        location.accuracy = accuracy
        location.speed = horizontalSpeed
        return location
    }
}

