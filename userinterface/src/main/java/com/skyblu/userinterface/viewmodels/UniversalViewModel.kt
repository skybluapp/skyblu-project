package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.firestore.toUser
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UniversalState(
    val thisUser : MutableState<String?> = mutableStateOf("ThisUser"),

)

@HiltViewModel
class UniversalViewModel @Inject constructor(
    private val authentication: AuthenticationInterface,
    val savedUsers: SavedUsersInterface,
    val readServer : ReadServerInterface,
    val writeServer : WriteServerInterface,
) : ViewModel() {

    val state by mutableStateOf(UniversalState())

    init{
        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest { user ->
                state.thisUser.value = user
                if(user != null && !savedUsers.containsUser(user)){
                    val result = readServer.getUser(user)
                    val user : User? = result.getOrNull()?.toUser()
                    if (user != null) {
                        savedUsers.addUser(user)
                    }
                }
            }
        }
    }

}