package com.skyblu.utilities

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



