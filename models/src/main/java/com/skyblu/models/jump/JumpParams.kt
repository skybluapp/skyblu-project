package com.skyblu.models.jump

import androidx.room.*
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Name of table for skydives for Room databases
 *@author Oliver Stocks
 */
const val JUMP_TABLE = "skydive_table"

/**
 * Contains default data for a skydive
 *@author Oliver Stocks
 */
object SkydiveDefaults{
    const val defaultTitle = "New Skydive"
    const val defaultJumpNumber = 0
    const val defaultStaticMapUrl = ""
    const val defaultAircraft = "Unknown Aircraft"
    const val defaultEquipment = "Unknown Equipment"
    const val defaultDropzone = "Unknown Dropzone"
    const val defaultDescription = "WaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffle" +
            "WaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffleWaffle" +
            ""
    const val defaultUploaded = false
    val currentDate = System.currentTimeMillis()
    val newSkydiveID = UUID.randomUUID().toString()

}

private val s = SkydiveDefaults.defaultDropzone

/**
 * Creates an instance of a Skydive data object
 * @author Oliver Stocks
 * @property jumpID A unique identifier for the skydive
 * @property title A title for the skydive
 * @property userID A unique identifier for the user
 * @property jumpNumber The number of skydives the user has done
 * @property date The date the skydive took place
 * @property aircraft The type of aircraft used on the skydive
 * @property equipment The type of canopy used on the skydive
 * @property dropzone Where the skydive took place
 * @property uploaded Has the skydive been uploaded to the server?
 * @property staticMapUrl A URL to collect a static map representing the skydive
 */
@Entity(tableName = JUMP_TABLE)
@Serializable
data class Jump(
    @PrimaryKey
    var jumpID : String = SkydiveDefaults.newSkydiveID,
    var userID : String,
    var jumpNumber : Int = SkydiveDefaults.defaultJumpNumber,
    var date : Long = SkydiveDefaults.currentDate,
    var title : String = SkydiveDefaults.defaultTitle,
    var aircraft : String = SkydiveDefaults.defaultAircraft,
    var equipment : String = SkydiveDefaults.defaultEquipment,
    var dropzone : String = SkydiveDefaults.defaultDropzone,
    var description : String = SkydiveDefaults.defaultDescription,
    var staticMapUrl : String = SkydiveDefaults.defaultStaticMapUrl,
    var uploaded : Boolean = SkydiveDefaults.defaultUploaded,
)

/**
 * A One-to-Many relationship between datapoints and skydives
 */
data class JumpWithDatapoints(
    @Embedded val jump : Jump,
    @Relation(
        parentColumn = "jumpID",
        entityColumn = "jumpID"
    )
    val datapoints: List<SkydiveDataPoint>
)

/**
 * Parameter names for a skydive
 */
object JumpParams{
    const val JUMP = "jump"
    const val JUMP_ID = "jumpID"
    const val USER_ID = "userID"
    const val JUMP_NUMBER = "jumpNumber"
    const val DATE = "date"
    const val EQUIPMENT = "equipment"
    const val DROPZONE = "dropzone"
    const val DESCRIPTION = "description"
    const val TITLE = "title"
    const val STATIC_MAP_URL = "staticMapUrl"
    const val UPLOADED = "uploaded"
    const val AIRCRAFT = "aircraft"
}


