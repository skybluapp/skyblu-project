package com.skyblu.data.users

import androidx.compose.animation.slideIn
import androidx.compose.runtime.mutableStateMapOf
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.models.jump.Skydiver
import timber.log.Timber
import javax.inject.Inject

class SavedUsers @Inject constructor(
    val authentication : AuthenticationInterface
) : SavedUsersInterface {

    override val skydiverMap : MutableMap<String, Skydiver> = mutableStateMapOf()

    override fun containsSkydiver(skydiver: String): Boolean {
        return skydiverMap.containsKey(skydiver)
    }

    override fun addSkydiver(skydiver: Skydiver) {
        Timber.d("Skydiver Added $skydiver")
        skydiverMap[skydiver.skydiverID] = skydiver
    }

    override fun thisSkydiver() : Skydiver?{
        return skydiverMap[authentication.getCurrentUser()]
    }

    override fun clear() {
        skydiverMap.clear()
    }
}