package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.authentication.FirebaseAuthentication
import com.skyblu.userinterface.screens.SettingsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

data class SettingsScreenState(
    val screen : MutableState<SettingsScreen> = mutableStateOf(SettingsScreen.ROOT)
)

@HiltViewModel
public class SettingsViewModel @Inject constructor(
  private val authentication : AuthenticationInterface
) : ViewModel(){

    val state by mutableStateOf(SettingsScreenState())


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
