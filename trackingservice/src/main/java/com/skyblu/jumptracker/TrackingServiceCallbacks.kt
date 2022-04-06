package com.skyblu.jumptracker

import android.location.Location
import com.skyblu.models.jump.SkydiveDataPoint

interface TrackingServiceCallbacks {
    fun postSkydiveDataPoint(dataPoint : SkydiveDataPoint)
    fun pressureSensorUnavailable()
    fun locationUnavailable()
}

class EmptyTrackingServiceCallbacks : TrackingServiceCallbacks{

    override fun postSkydiveDataPoint(dataPoint: SkydiveDataPoint) {

    }

    override fun pressureSensorUnavailable() {

    }
    override fun locationUnavailable() {

    }
}