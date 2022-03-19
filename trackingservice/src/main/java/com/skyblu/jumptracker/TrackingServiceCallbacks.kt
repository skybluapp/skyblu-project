package com.skyblu.jumptracker

import android.location.Location

interface TrackingServiceCallbacks {
    fun postLocation(
        location: Location,
        pressure: Float,
        altitudeFt: Float,
        timeStamp: Long
    )
    fun pressureSensorUnavailable()
    fun locationUnavailable()
}