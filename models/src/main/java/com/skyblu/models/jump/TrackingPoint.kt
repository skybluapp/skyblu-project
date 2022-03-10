package com.skyblu.models.jump

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.skyblu.models.jump.JumpPhase

data class TrackingPoint(
    var location : LatLng,
    var airPressure : Float,
    val jumpPhase: JumpPhase = JumpPhase.UNKNOWN
)