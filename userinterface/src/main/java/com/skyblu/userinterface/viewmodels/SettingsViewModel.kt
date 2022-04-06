package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.authentication.FirebaseAuthentication
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
public class SettingsViewModel @Inject constructor(
  private val authentication : AuthenticationInterface
) : ViewModel(){

    val username : MutableState<String> = mutableStateOf("")
    val password : MutableState<String> = mutableStateOf("")
    val loggedIn : MutableState<Boolean> = mutableStateOf(false)

    init{
        loggedIn.value = authentication.getCurrentUser() != null
    }


    fun logout(){
        Timber.d("Authentication" + authentication.getCurrentUser())
        authentication.logout()
        Timber.d("Authentication" + authentication.getCurrentUser())
        loggedIn.value = false

    }

  fun currentUser() : String?{
    return authentication.getCurrentUser()
  }

}
