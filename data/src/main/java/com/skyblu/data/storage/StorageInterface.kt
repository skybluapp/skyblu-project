package com.skyblu.data.storage

import android.content.Context
import android.net.Uri
import com.skyblu.models.jump.Skydiver

interface StorageInterface {
    suspend fun uploadProfilePicture(applicationContext : Context, skydiverID : String, skydiver : Skydiver, uri : Uri)
    fun getProfilePicture(skydiverID : String) : Result<String?>
}