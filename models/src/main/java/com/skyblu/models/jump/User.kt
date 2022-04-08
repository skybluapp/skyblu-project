package com.skyblu.models.jump

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skyblu.configuration.UNKNOWN_USER_STRING
import kotlinx.serialization.Serializable

/**
 * @author Oliver Stocks
 *
 */
@Entity(tableName = "user_table")
@Serializable
data class User(
    @PrimaryKey
    var ID : String = UNKNOWN_USER_STRING,
    var jumpNumber : Int? = 0,
    var photoUrl : String? = "",
    var username : String = UNKNOWN_USER_STRING,
    var bio : String = "",
    var licence : Licence? = Licence.A,
)

enum class Licence{
    A,
    B,
    C,
    D
}

/**
 * Parameter names for a skydive
 */
object UserParameterNames{
    const val USER = "user"
    const val ID = "id"
    const val PHOTO_URL = "photoUrl"
    const val USERNAME = "username"
    const val BIO = "bio"
    const val LICENCE = "licence"
    const val JUMP_NUMBER = "jumpNumber"
}