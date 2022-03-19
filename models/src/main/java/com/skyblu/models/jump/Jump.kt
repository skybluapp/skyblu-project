package com.skyblu.models.jump

import android.util.Log
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.android.gms.maps.model.LatLng
import com.skyblu.utilities.hpaToFeet
import java.util.*

//@Entity
data class Jump(
    @PrimaryKey
    var jumpId : String = UUID.randomUUID().toString(),
    var title : String = "New Jump",

    @Embedded
    var trackingData : JumpTrackingData ,
    val userId : String,
    var jumpNumber : Int = 0,
    val date : Date = Date(),
    var aircraft : String,
    var equipment : String,
    var dropzone : String,
    var description : String
)
//
//@Entity
//data class JumpAndTrackingData(
//    @Embedded val jump : Jump,
//    @Relation(
//        parentColumn = "jumpId",
//        entityColumn = "ownerJumpId"
//    )
//    val jumpTrackingData: JumpTrackingData
//)

