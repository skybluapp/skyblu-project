package com.skyblu.userinterface.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.storage.StorageInterface
import com.skyblu.data.users.SavedUsersInterface
//import com.skyblu.models.jump.Licence
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    val storage: StorageInterface,
    val authentication: AuthenticationInterface,
    val savedUsers : SavedUsersInterface,
    @Named("AppContext") private val context: Context,

) : ViewModel() {

    var state by mutableStateOf(AccountSettingsState())

    init{
        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest {
                state.thisUser.value = it
            }
        }
        val thisUser = savedUsers.thisUser()
        if(thisUser != null){
            state.username.value = thisUser.username
            state.bio.value = thisUser.bio
            state.photoUrl = thisUser.photoUrl
            Timber.d("URL : ${thisUser.photoUrl}")
        }
        
    }

    fun save() {
        viewModelScope.launch {
            if (state.photoUri != null) {
                storage.uploadProfilePicture(
                    applicationContext = context,
                    uri = state.photoUri!!,
                    userID = authentication.getCurrentUser()!!,
                    user = User(
                        ID = state.thisUser.value!!,
                        jumpNumber = state.jumpNumber.value,
                        username = state.username.value,
                        bio = state.bio.value,
//                        licence = state.licence.value
                    )
                )
            }
        }
    }

    fun setPhotoUri(uri: Uri?) {
        state.photoUri = uri
    }
}

data class AccountSettingsState(
    var photoUri: Uri? = null,
    var photoUrl : String? = null,
    var username: MutableState<String> = mutableStateOf(""),
    var bio: MutableState<String> = mutableStateOf(""),
    var jumpNumber : MutableState<Int> = mutableStateOf(0),
    var thisUser: MutableState<String?> = mutableStateOf("user"),
//    var licence: MutableState<Licence> = mutableStateOf(Licence.A)
)


