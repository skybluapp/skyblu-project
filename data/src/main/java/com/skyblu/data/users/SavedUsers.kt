package com.skyblu.data.users

import androidx.compose.runtime.mutableStateMapOf
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import timber.log.Timber
import javax.inject.Inject

class SavedUsers @Inject constructor(
    val authentication : AuthenticationInterface
) : SavedUsersInterface {

    override val userMap : MutableMap<String, User> = mutableStateMapOf()

    override fun containsUser(user: String): Boolean {
        return userMap.containsKey(user)
    }

    override fun addUser(user: User) {
        Timber.d("User Added $user")
        userMap[user.ID] = user
    }

    override fun thisUser() : User?{
        return userMap[authentication.getCurrentUser()]
    }

    override fun clear() {
        userMap.clear()
    }
}

class SavedSkydives @Inject constructor() : SavedSkydivesInterface{
    override var skydive : Jump? = null
}