package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.configuration.Concept
import com.skyblu.configuration.LARGE_PADDING
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.componants.data.ProfileHeader
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.lists.PagingList
import com.skyblu.userinterface.componants.scaffold.AppBottomAppBar
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.ProfileViewModel
import com.skyblu.userinterface.viewmodels.UniversalViewModel


@Composable()
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    universalViewModel: UniversalViewModel,
    userID: String
) {
    val appState = universalViewModel.state
    val screenState = viewModel.state
    val savedUsers = universalViewModel.savedUsers.userMap

    val navIcon = Concept.Profile

    LaunchedEffect(
        key1 = true,
        block = {
            viewModel.setUser(userID)
        }
    )

    LaunchedEffect(
        key1 = appState.thisUser.value,
        block = {
            if (appState.thisUser.value.isNullOrBlank()) {
                navController.navigate(Concept.LoggedOut.route)
            }
        }
    )


    val navMenuAction: List<ActionConcept> =
        listOf(
            ActionConcept(
                action = { navController.popBackStack() },
                concept = Concept.Previous
            )
        )
    val menuAction: List<ActionConcept> = if (screenState.profileUser.value == screenState.profileUser.value) {
        listOf(
            ActionConcept(
                action = { navController.navigate(Concept.Settings.route) },
                concept = Concept.Settings
            )
        )
    } else {
        listOf()
    }



    Scaffold(
        topBar = {
            AppTopAppBar(
                title = navIcon.title,
                navigationIcon = { ActionConceptList(menuActions = navMenuAction) },
                actionIcons = { ActionConceptList(menuActions = menuAction) }
            )
        },
        bottomBar = {
            if(userID == appState.thisUser.value) {
                AppBottomAppBar(
                    navController = navController,
                    userID
                )
            }
        },
        content = {
            PagingList<Jump>(
                Heading = {
                    ProfileHeader(
                        universalViewModel.savedUsers.userMap[userID] ?: User()
                    )
                    Spacer(Modifier.height(LARGE_PADDING))
                },
                list = screenState.skydives,
                endReached = screenState.endReached,
                isLoading = screenState.isLoading.value,
                loadNextPage = { viewModel.loadNextSkydivePage() },
                Content = { skydive ->
                    val user = universalViewModel.savedUsers.userMap[userID]
                    JumpCard(
                        skydive = skydive,
                        onMapClick = { navController.navigate(Concept.Map.route + "/" + skydive.jumpID) },
                        username = universalViewModel.savedUsers.userMap[userID]?.username
                            ?: "unknown",
                        user = savedUsers[userID] ?: User(),
                        isMine = savedUsers[userID]?.ID ?: "" == skydive.userID,
                        onProfileClicked = {

                        },
                        onEditClicked = {
                            viewModel.savedSkydives.skydive = skydive
                            navController.navigate(Concept.Edit.route)
                        }
                    )
                },
                swipeState = screenState.swipeRefreshState.value,
                refresh = {
                    viewModel.setUser(userID)
                }
            )
        },
    )
}

const val TIMEOUT_MILLIS = 10000

fun timeout(startTime: Long): Boolean {
    return System.currentTimeMillis() - TIMEOUT_MILLIS > startTime
}

