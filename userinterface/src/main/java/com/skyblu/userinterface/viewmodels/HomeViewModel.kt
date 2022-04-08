package com.skyblu.userinterface.viewmodels

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.firestore.DocumentSnapshot
import com.skyblu.configuration.PERMISSIONS
import com.skyblu.configuration.PermissionsInterfaceImpl
import com.skyblu.configuration.JUMP_PAGE_SIZE
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.firestore.toSkydive
import com.skyblu.data.firestore.toUser
import com.skyblu.data.pagination.GenericPaginator
import com.skyblu.data.room.TrackingPointsDao
import com.skyblu.data.users.SavedSkydives
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
 */
data class HomeState(
    var isLoading: MutableState<Boolean> = mutableStateOf(false),
    var skydives: MutableList<Jump> = mutableListOf(),
    var error: String? = null,
    var endReached: Boolean = false,
    var page: DocumentSnapshot? = null,
    val isRefreshing: MutableState<Boolean> = mutableStateOf(false),
    val swipeRefreshState: MutableState<SwipeRefreshState> = mutableStateOf(SwipeRefreshState(isRefreshing = isRefreshing.value)),
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
    val readServer : ReadServerInterface,
    val writeServer : WriteServerInterface,
    private val savedUsers: SavedUsersInterface,
    val savedSkydives: SavedSkydives,
    @ApplicationContext context: Context
) : ViewModel() {

    /**
     * Create new state
     */
    var state by mutableStateOf(HomeState())

    /**
     * Pagination manages paged content for the Home Screen
     */
    private val paginator = GenericPaginator(
        initialKey = state.page,
        onRequest = { nextPage ->
            readServer.getJumps(
                nextPage,
                JUMP_PAGE_SIZE
            )
        },
        onLoadUpdated = {
            state.isLoading.value = it
        },
        onSuccess = { list, newKey ->
            state.page = newKey
            state.endReached = list.documents.isEmpty()

            Timber.d("Retrieved ${list.documents.size} Documents")
            /**
             * For each document received, convert it to a Skydive and add it to the list
             */
            for (document in list.documents) {
                val skydive = document.toSkydive()
                state.skydives.add(skydive)
                Timber.d(state.skydives.size.toString())

                /**
                 * If the user has not been saved, get the user from the server and store their details
                 */
                if (!savedUsers.containsUser(skydive.userID)) {
                    viewModelScope.launch {
                        val result = readServer.getUser(skydive.userID)
                        val user: User? = result.getOrNull()?.toUser()
                        if (user != null) {
                            Timber.d("User Collected $user")
                            savedUsers.addUser(user = user)
                        }
                    }
                }
            }
        },
        onError = { error ->
            Timber.d("Home Request error!")
            if (error != null) {
                state.error = error.message
            }
        },
        getNextKey = { list ->
            list.lastOrNull()
        },
    )

    /**
     * Load first page on initialisation
     */
    init {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    /**
     * Loads the next page
     */
    fun loadNextSkydivePage() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    /**
     * Refreshes the list of skydives
     */
    fun refresh() {
        state.skydives.clear()
        paginator.reset()
        loadNextSkydivePage()
    }

    /**
     * Checks if permissions have been granted
     */
    fun checkPermissions(activity: Activity): Boolean {
        val permissionInterface = PermissionsInterfaceImpl(activity = activity)
        return permissionInterface.checkPermissions(PERMISSIONS)
    }

    /**
     * Request location permissions for tracking skydives
     */
    fun requestPermissions(activity: Activity) {
        val permissionInterface = PermissionsInterfaceImpl(activity = activity)
        permissionInterface.requestPermission(PERMISSIONS)
    }
}

