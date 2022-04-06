package com.skyblu.data.authentication

import android.content.Context
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthenticationInterface {
    fun getCurrentUser() : String?
    fun login(email : String, password : String, onSuccess : (String) -> Unit, onFailure : (String) -> Unit)
    fun createAccount(email : String, password: String, confirm : String, onSuccess : (String) -> Unit, onFailure : (String) -> Unit)
    fun logout()
    fun initialise(context : Context)
    val loggedInFlow : Flow<String?>
}