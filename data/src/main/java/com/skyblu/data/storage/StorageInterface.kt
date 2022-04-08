package com.skyblu.data.storage

import android.content.Context
import android.net.Uri
import com.skyblu.models.jump.User

interface StorageInterface {
    suspend fun uploadProfilePicture(applicationContext : Context, userID : String, user : User, uri : Uri)
    fun getProfilePicture(userID : String) : Result<String?>
}