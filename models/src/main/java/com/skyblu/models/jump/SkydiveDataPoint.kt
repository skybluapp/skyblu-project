package com.skyblu.models.jump

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.absoluteValue

/**
 * Name of table for skydive data points for Room databases
 *@author Oliver Stocks
 */
const val SKYDIVE_DATA_POINT_TABLE = "skydive_data_point_table"

/**
 * A representation of a single point during a skydive
 * @author Oliver Stocks
 * @property dataPointID A unique identifier for the data point
 * @property skydiveID A unique identifier for the skydive
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
@Entity(SKYDIVE_DATA_POINT_TABLE)
data class SkydiveDataPoint(
    @PrimaryKey
    val dataPointID: String,
    val skydiveID: String,
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
object DatapointParameters{
    const val DATAPOINT_ID = "dataPointID"
    const val SKYDIVE_ID = "skydiveID"
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
enum class SkydivePhase {
    /**
     * The skydiver has begun tracking, but is not yet in the aircraft
     */
    WALKING,
    /**
     * The skydiver is in the aircraft ascending to altitude
     */
    AIRCRAFT,
    /**
    The skydiver has exited the aircraft and is in freefall descending quickly
     **/
    FREEFALL,
    /*
    The skydiver has deployed their parachute and is descending slowly
     */
    CANOPY,
    /*
    The skydiver has landed and is now longer descending
     */
    LANDED,
    /*
    It is not known what phase of the skydive the skydiver is in
     */
    UNKNOWN
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
fun List<SkydiveDataPoint>.maxVerticalSpeed(direction: VerticalDirection?): Float {
    return when (direction) {
        VerticalDirection.DOWNWARD -> {
            minOf { skydiveDataPoint: SkydiveDataPoint -> skydiveDataPoint.verticalSpeed  }
        }
        VerticalDirection.UPWARD -> {
            maxOf { skydiveDataPoint: SkydiveDataPoint -> skydiveDataPoint.verticalSpeed  }
        }
        null -> {
            maxOf { skydiveDataPoint: SkydiveDataPoint -> skydiveDataPoint.verticalSpeed.absoluteValue  }
        }
    }
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
fun List<SkydiveDataPoint>.maxGroundSpeed(): Float {
    return maxOf { skydiveDataPoint -> skydiveDataPoint.groundSpeed }
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
fun List<SkydiveDataPoint>.maxLatitude(): Double {
    return maxOf { skydiveDataPoint -> skydiveDataPoint.latitude }
}

/**
 * @return The minimum latitude  from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.minLatitude(): Double {
    return minOf { skydiveDataPoint -> skydiveDataPoint.latitude }
}

/**
 * @return The maximum longitude from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.maxLongitude(): Double {
    return maxOf { skydiveDataPoint -> skydiveDataPoint.longitude }
}

/**
 * @return The minimum longitude  from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.minLongitude(): Double {
    return minOf { skydiveDataPoint -> skydiveDataPoint.longitude }
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
fun List<SkydiveDataPoint>.centerPoint(): LatLng{
    return LatLng( sumOf { point -> point.latitude } / size, sumOf { point -> point.longitude } / size)
}

/**
 * @return Get the most southeasterly point of a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.southwest(): LatLng{
    return LatLng( minLatitude(), minLongitude())
}

/**
 * @return Get the most northwesterly point of a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.northeast(): LatLng{
    return LatLng( maxLatitude(), maxLongitude())
}

/** @return Get bounds from a list of skydiving tracking points
 *
 */
fun List<SkydiveDataPoint>.bounds(): LatLngBounds{
    return LatLngBounds(southwest(), northeast())
}

/**
 * @return Return a list of LatLng points from a list of skydiving tracking points
 */
fun List<SkydiveDataPoint>.latLngList(): List<LatLng>{
    val latLngList : MutableList<LatLng> = mutableListOf()
    for(i in indices){
        latLngList.add(LatLng(elementAt(i).latitude, elementAt(i).longitude))
    }
    return latLngList
}

fun List<SkydiveDataPoint>.newest() : SkydiveDataPoint?{
    return maxByOrNull { it.timeStamp }
}

fun List<SkydiveDataPoint>.oldest() : SkydiveDataPoint?{
    return minByOrNull { it.timeStamp }
}

fun List<SkydiveDataPoint>.filterByPhase(phase : SkydivePhase) : List<SkydiveDataPoint>{
    return filter { point -> point.phase == phase }.sortedBy { it.timeStamp }
}


