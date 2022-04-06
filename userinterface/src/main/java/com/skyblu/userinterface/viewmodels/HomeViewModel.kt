package com.skyblu.userinterface.viewmodels

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.skyblu.configuration.PermissionsInterfaceImpl
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.ServerInterface
import com.skyblu.data.pagination.GenericPaginator
import com.skyblu.data.room.TrackingPointsDao
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.models.jump.Skydive
import com.skyblu.models.jump.SkydiveParameterNames
import com.skyblu.models.jump.Skydiver
import com.skyblu.models.jump.SkydiverParameterNames
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Contains the current state of the Home Screen
 * @param isLoading True if the skydive list is currently loading
 * @param skydives The current list of skydives that have been loaded
 * @param error Contains a string if an error has occurred
 * @param endReached True if there is no more content to be loaded from the
 * @param page The document that acts as the key to access more content
 * @param userMapping A mapping between skydiverID's and usernames
 */
data class HomeState(
    val isLoading: Boolean = false,
    var skydives: MutableList<Skydive> = mutableListOf(),
    val error: String? = null,
    var endReached: Boolean = false,
    val page: DocumentSnapshot? = null,
    var userMapping : MutableMap<String, Skydiver> = mutableStateMapOf<String, Skydiver>(),
    val thisUser: MutableState<String?> = mutableStateOf("user"),

)

/**
 * ViewModel for Home Screen
 * @param room Provide interface to access Room local database
 * @param authentication provide interface to access authentication functions
 * @param server provide interface to access remote backend server
 * @property state current state of the Home Screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val room: TrackingPointsDao,
    private val authentication: AuthenticationInterface,
    private val server: ServerInterface,
    val savedUsers : SavedUsersInterface,
    @ApplicationContext context: Context
) : ViewModel() {



    var state by mutableStateOf(HomeState())



    private val paginator = GenericPaginator(
        initialKey = state.page,
        onLoadUpdated = {
            state = state.copy(isLoading = it)
        },
        getNextKey = { list ->
            if (list.documents.isEmpty()) {
                state.endReached = true
                null
            } else {
                list.documents[list.size() - 1]
            }
        },
        onError = {
            state = state.copy(error = it?.message)
        },
        onSuccess = { querySnapshot, newKey ->
            state.copy(
                page = newKey,
                endReached = querySnapshot.documents.isEmpty()
            )
            for (skydive in querySnapshot.documents) {
                val skydiverID = skydive[SkydiveParameterNames.SKYDIVER_ID].toString()
                state.skydives.add(createSkydive(document = skydive))

                if(!savedUsers.containsSkydiver(skydiverID)){

                    viewModelScope.launch {
                        val skydiverDocument = server.getSkydiverFromServer(skydiverID)
                        val snapshot : DocumentSnapshot? = skydiverDocument.getOrNull()
                        savedUsers.addSkydiver(createUser(snapshot))
                    }
                }

                if(!state.userMapping.containsKey(skydiverID)){
                    state.userMapping[skydiverID] = Skydiver()
                    viewModelScope.launch {
                        val skydiverDocument = server.getSkydiverFromServer(skydiverID)
                        val snapshot : DocumentSnapshot? = skydiverDocument.getOrNull()
                        state.userMapping[skydiverID] = createUser(snapshot)
                    }
                }
            }
        },
        onRequest = { nextPage ->
            server.getSkydivesFromServer(
                nextPage,
                3
            )
        }
    )

    init {
        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest {
                state.thisUser.value = it
                if(it != null){
                    val skydiverDocument = server.getSkydiverFromServer(it)
                    val snapshot : DocumentSnapshot? = skydiverDocument.getOrNull()
                    state.userMapping[it] = createUser(snapshot)
                }

            }
        }
        viewModelScope.launch {
            paginator.loadNextItems()
        }
        state.userMapping = savedUsers.skydiverMap
    }


    fun loadNextSkydivePage() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun refresh() {
        state = HomeState()
        paginator.reset()
        savedUsers.clear()
        viewModelScope.launch {
            val currentUser = authentication.getCurrentUser()

            if(currentUser != null){
                val p = server.getSkydiverFromServer(currentUser)
                delay(3000)
                val snapshot = p.getOrNull()
                val user = createUser(snapshot)
                Timber.d("This User is" + currentUser + user.username)
                savedUsers.addSkydiver(createUser(snapshot))
            }
        }
        viewModelScope.launch { paginator.loadNextItems() }
    }

    fun checkPermissions(activity: Activity): Boolean {
        val permissionInterface = PermissionsInterfaceImpl(activity = activity)
        return permissionInterface.checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun requestPermissions(activity: Activity) {
        val permissionInterface = PermissionsInterfaceImpl(activity = activity)
        permissionInterface.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun createSkydive(document : DocumentSnapshot) : Skydive{
        return Skydive(
            skydiveID = document[SkydiveParameterNames.SKYDIVE_ID].toString(),
            date = document[SkydiveParameterNames.DATE].toString().toLong(),
            description = document[SkydiveParameterNames.DESCRIPTION].toString(),
            skydiveNumber = document[SkydiveParameterNames.SKYDIVE_NUMBER].toString().toInt(),
            skydiverID = document[SkydiveParameterNames.SKYDIVER_ID].toString(),
            staticMapUrl = document[SkydiveParameterNames.STATIC_MAP_URL].toString(),
            uploaded = document[SkydiveParameterNames.UPLOADED].toString().toBoolean(),
            title = document[SkydiveParameterNames.TITLE].toString(),
            equipment = document[SkydiveParameterNames.EQUIPMENT].toString(),
            dropzone = document[SkydiveParameterNames.DROPZONE].toString(),
            aircraft = document[SkydiveParameterNames.AIRCRAFT].toString()
        )
    }


    private fun createUser(document : DocumentSnapshot?) : Skydiver{
        var s = Skydiver()

        if(document == null){
            return s
        } else {
             s = Skydiver(
                skydiverID = document.id,
                username = document[SkydiverParameterNames.USERNAME].toString(),
                bio = document[SkydiverParameterNames.BIO].toString(),
                skydiverPhotoUrl = document[SkydiverParameterNames.SKYDIVER_PHOTO_URL].toString()
            )
        }

        return s
    }
}

