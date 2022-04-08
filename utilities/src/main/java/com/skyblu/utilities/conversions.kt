package com.skyblu.utilities

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


fun Float.hpaToMeters() : Float{
    return  (44330 * (1 - (this/1013.25).pow(1/5.255))).toFloat()
}
fun Float.hpaToFeet() : Float{
    return (3.28084 * this.hpaToMeters()).toFloat()
}

fun Float.feetToMeters() : Float{
    return (this / 3.28084).toFloat()
}

fun Float.metersToFeet() : Float{
    return (this * 3.28084).toFloat()
}

fun Long.millisToDateString() : String {
    val date = Date(this)
    val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy")
    return dateFormat.format(date)
}


