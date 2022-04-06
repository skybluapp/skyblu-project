package com.skyblu.data.users

import androidx.compose.runtime.MutableState
import com.skyblu.models.jump.Skydiver

interface SavedUsersInterface {
    val skydiverMap :  MutableMap<String, Skydiver>
    fun containsSkydiver(skydiver : String) : Boolean
    fun addSkydiver(skydiver : Skydiver)
    fun clear()
    fun thisSkydiver() : Skydiver?
}