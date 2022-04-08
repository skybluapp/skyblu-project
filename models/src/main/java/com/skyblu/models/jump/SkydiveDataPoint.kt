package com.skyblu.models.jump

import android.location.Location
import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.skyblu.configuration.AIRCRAFT_STRING
import com.skyblu.models.R
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.absoluteValue

/**
 * Name of table for skydive data points for Room databases
 *@author Oliver Stocks
 */
const val JUMP_DATA_POINT_TABLE = "jump_data_point_table"

/**
 * A representation of a single point during a skydive
 * @author Oliver Stocks
 * @property dataPointID A unique identifier for the data point
 * @property jumpID A unique identifier for the skydive
 * @property latitude The latitude of the point
 * @property longitude The Longitude of the point
 * @property airPressure The Air Pressure of the current Point in hPa
 * @property altitude The Altitude of the current point in meters
 * @property timeStamp The timestamp the moment the datapoint was recorded
 * @property verticalSpeed The current vertical speed the moment the datapoint was recorded in m/s
 * @property groundSpeed The speed across the ground the moment the datapoint was recorded
 * @property phase The current phase in the skydive
 */
@Serializable
@Entity(JUMP_DATA_POINT_TABLE)
data class SkydiveDataPoint(
    @PrimaryKey
    val dataPointID: String,
    val jumpID: String,
    var latitude: Double,
    var longitude: Double,
    var airPressure: Float,
    var altitude: Float,
    val timeStamp: Long,
    val verticalSpeed: Float,
    val groundSpeed: Float,
    var phase: SkydivePhase = SkydivePhase.UNKNOWN
)

/**
 * Parameter names for a skydive datapoint
 */
object DatapointParams {

    const val DATAPOINT = "datapoint"
    const val DATAPOINT_ID = "dataPointID"
    const val JUMP_ID = "jumpID"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val AIR_PRESSURE = "airPressure"
    const val ALTITUDE = "altitude"
    const val TIMESTAMP = "timeStamp"
    const val VERTICAL_SPEED = "verticalSpeed"
    const val GROUND_SPEED = "groundSpeed"
    const val PHASE = "phase"
}

/**
 * The phase of a skydive
 */
enum class SkydivePhase(
    val title: String,
    @DrawableRes val icon: Int
) {

    /**
     * The user has begun tracking, but is not yet in the aircraft
     */
    WALKING(
        "Walking",
        R.drawable.walk
    ),

    /**
     * The user is in the aircraft ascending to altitude
     */
    AIRCRAFT(
        AIRCRAFT_STRING,
        R.drawable.aircraft
    ),

    /**
    The user has exited the aircraft and is in freefall descending quickly
     **/
    FREEFALL(
        "Freefall",
        R.drawable.freefall
    ),

    /*
    The user has deployed their parachute and is descending slowly
     */
    CANOPY(
        "Canopy",
        R.drawable.parachute
    ),

    /*
    The user has landed and is now longer descending
     */
    LANDED(
        "Landed",
        R.drawable.walk
    ),

    /*
    It is not known what phase of the skydive the user is in
     */
    UNKNOWN(
        "Unknown",
        R.drawable.unknown
    )
}

/**
 * Functions convert complex types in to types that can be stored in a Room database (only used internally by Room DB)

 */
class SkydiveDataPointConverters {

    @TypeConverter
    fun phaseToString(phase: SkydivePhase) = phase.name

    @TypeConverter
    fun stringToPhase(string: String) = enumValueOf<SkydivePhase>(string)

    @TypeConverter
    fun timestampToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

/**
 * @return The maximum vertical speed of a list of Skydiving tracking points in a specified direction, or of either donation if direction is unspecified
 * @param direction The direction of maximum speed
 */
fun List<SkydiveDataPoint>.maxVerticalSpeed(direction: VerticalDirection?): Float? {
    return when (direction) {
        VerticalDirection.DOWNWARD -> {
            minOfOrNull { skydiveDataPoint: SkydiveDataPoint -> skydiveDataPoint.verticalSpeed }
        }
        VerticalDirection.UPWARD -> {
            maxOfOrNull { skydiveDataPoint: SkydiveDataPoint -> skydiveDataPoint.verticalSpeed }
        }
        null -> {
            maxOf { skydiveDataPoint: SkydiveDataPoint -> skydiveDataPoint.verticalSpeed.absoluteValue }
        }
    }
}

fun List<SkydiveDataPoint>.averageVerticalSpeed(): Double {
    return sumOf { skydiveDataPoint: SkydiveDataPoint -> skydiveDataPoint.verticalSpeed.toDouble() } / size.toDouble()
}
fun List<SkydiveDataPoint>.averageGroundSpeed(): Double {
    return sumOf { skydiveDataPoint: SkydiveDataPoint -> skydiveDataPoint.groundSpeed.toDouble() } / size.toDouble()
}

/**
 * Enum to specify vertical direction of travel
 */
enum class VerticalDirection {

    /**
     * Upward direction of travel
     */
    UPWARD,

    /**
     * Downward direction of travel
     */
    DOWNWARD
}

/**
 * @return The maximum ground speed from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.maxGroundSpeed(): Float? {

    return maxOfOrNull { skydiveDataPoint -> skydiveDataPoint.groundSpeed }
}

/**
 * @return The minimum ground speed from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.minGroundSpeed(): Float {
    return minOf { skydiveDataPoint -> skydiveDataPoint.groundSpeed }
}

/**
 * @return The maximum latitude from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.maxLatitude(): Double? {
    return maxOfOrNull { skydiveDataPoint -> skydiveDataPoint.latitude }
}

/**
 * @return The minimum latitude  from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.minLatitude(): Double? {
    return minOfOrNull { skydiveDataPoint -> skydiveDataPoint.latitude }
}

/**
 * @return The maximum longitude from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.maxLongitude(): Double? {
    return maxOfOrNull { skydiveDataPoint -> skydiveDataPoint.longitude }
}

/**
 * @return The minimum longitude  from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.minLongitude(): Double? {
    return minOfOrNull { skydiveDataPoint -> skydiveDataPoint.longitude }
}

/**
 * Returns the maximum altitude from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.maxAltitude(): Float {
    return maxOf { skydiveDataPoint -> skydiveDataPoint.altitude }
}

/**
 * @return The minimum altitude  from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.minAltitude(): Float {
    return minOf { skydiveDataPoint -> skydiveDataPoint.altitude }
}

/**
 * @return Centre point of skydiving tracking points
 */
fun List<SkydiveDataPoint>.centerPoint(): LatLng {
    return LatLng(sumOf { point -> point.latitude } / size,
        sumOf { point -> point.longitude } / size)
}

/**
 * @return Get the most southeasterly point of a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.southwest(): LatLng? {
    return minLatitude()?.let {
        minLongitude()?.let { it1 ->
            LatLng(
                it,
                it1
        )
        }
    }
}

/**
 * @return Get the most northwesterly point of a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.northeast(): LatLng? {
    return maxLatitude()?.let {
        maxLongitude()?.let { it1 ->
            LatLng(
                it,
                it1
        )
        }
    }
}

/** @return Get bounds from a list of skydiving tracking points
 *
 */
fun List<SkydiveDataPoint>.bounds(): LatLngBounds? {
    return northeast()?.let {
        southwest()?.let { it1 ->
            LatLngBounds(
                it1,
                it
        )
        }
    }
}

/**
 * @return Return a list of LatLng points from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.latLngList(): List<LatLng> {
    val latLngList: MutableList<LatLng> = mutableListOf()
    for (i in indices) {
        latLngList.add(
            LatLng(
                elementAt(i).latitude,
                elementAt(i).longitude
            )
        )
    }
    return latLngList
}

fun List<SkydiveDataPoint>.newest(): SkydiveDataPoint? {
    return maxByOrNull { it.timeStamp }
}

fun List<SkydiveDataPoint>.oldest(): SkydiveDataPoint? {
    return minByOrNull { it.timeStamp }
}

fun List<SkydiveDataPoint>.filterByPhase(phase: SkydivePhase): List<SkydiveDataPoint> {
    return filter { point -> point.phase == phase }.sortedBy { it.timeStamp }
}

fun List<SkydiveDataPoint>.calculateDistanceOfList() : Float{
    var oneStartIndex = 0
    var twoStartIndex = 1
    var totalDistance = 0f

    while(twoStartIndex <= indices.last){
        val one = Location("LocationOne")
        one.latitude = this[oneStartIndex].latitude
        one.longitude = this[oneStartIndex].longitude
        val two = Location("LocationTwo")
        two.latitude = this[twoStartIndex].latitude
        two.longitude = this[twoStartIndex].longitude
        totalDistance += one.distanceTo(two)
        oneStartIndex++
        twoStartIndex++
    }
    return totalDistance



}


