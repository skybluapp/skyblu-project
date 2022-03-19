package com.skyblu.models.jump

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.skyblu.models.jump.JumpPhase
import java.util.*

@TypeConverters()
@Entity
data class TrackingPoint(
    @PrimaryKey
    val trackingPointId: String,
    var latitude : Double,
    var longitude : Double,
    var airPressure: Float,
    val altitude: Float,
    val timeStamp: Long
)
