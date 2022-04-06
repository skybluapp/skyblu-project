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
import com.skyblu.data.authentication.FirebaseAuthentication
import com.skyblu.data.storage.StorageInterface
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.models.jump.Skydiver
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
        val thisSkydiver = savedUsers.thisSkydiver()
        if(thisSkydiver != null){
            state.username.value = thisSkydiver.username
            state.bio.value = thisSkydiver.bio
            state.photoUrl = thisSkydiver.skydiverPhotoUrl
            Timber.d("URL : ${thisSkydiver.skydiverPhotoUrl}")
        }
        
    }

    fun save() {
        viewModelScope.launch {
            if (state.photoUri != null) {
                storage.uploadProfilePicture(
                    applicationContext = context,
                    uri = state.photoUri!!,
                    skydiverID = "hello",
                    skydiver = Skydiver(
                        skydiverID = state.thisUser.value!!,
                        "",
                        username = state.username.value,
                        bio = state.bio.value
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
    var thisUser: MutableState<String?> = mutableStateOf("user"),
)


