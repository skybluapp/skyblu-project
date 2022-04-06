package com.skyblu.models.jump

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * @author Oliver Stocks
 *
 */
@Entity(tableName = "SKYDIVER_TABLE")
@Serializable
data class Skydiver(
    @PrimaryKey
    var skydiverID : String = "Unknown User",
    var skydiverPhotoUrl : String? = "",
    var username : String = "Skyblu User",
    var bio : String = ""
)

/**
 * Parameter names for a skydive
 */
object SkydiverParameterNames{
    const val SKYDIVER_ID = "skydiverID"
    const val SKYDIVER_PHOTO_URL = "skydiverPhotoUrl"
    const val USERNAME = "username"
    const val BIO = "bio"
}