package com.skyblu.userinterface.screens

import android.app.Activity
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyblu.configuration.Concept
import com.skyblu.configuration.HOME_STRING
import com.skyblu.configuration.UNKNOWN_USER_STRING
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.componants.lists.PagingList
import com.skyblu.userinterface.componants.scaffold.AppBottomAppBar
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.HomeViewModel
import com.skyblu.userinterface.viewmodels.UniversalViewModel

@Composable
fun HomeScreen(
    navController: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel(),
    universalViewModel: UniversalViewModel
) {

    val appState = universalViewModel.state
    val screenState = viewModel.state
    val savedUsers = universalViewModel.savedUsers.userMap

    val activity = LocalContext.current as Activity

    LaunchedEffect(
        key1 = universalViewModel.state.thisUser.value,
        block = {
            if (appState.thisUser.value.isNullOrBlank()) {
                navController.navigate(Concept.LoggedOut.route)
            }
        }
    )


    Scaffold(
        content = {
                  PagingList<Jump>(
                      Heading = {},
                      list = screenState.skydives,
                      endReached = screenState.endReached,
                      isLoading = screenState.isLoading.value,
                      loadNextPage = {viewModel.loadNextSkydivePage()},
                      refresh = {
                          screenState.isRefreshing.value = true
                          viewModel.refresh()
                                },
                      swipeState = screenState.swipeRefreshState.value,
                      Content = { skydive ->

                          JumpCard(
                              skydive = skydive,
                              onMapClick = { navController.navigate(  "${Concept.Map.route}/${skydive.jumpID}"  ) },
                              username = savedUsers[skydive.userID]?.username
                                  ?: UNKNOWN_USER_STRING,
                              user = savedUsers[skydive.userID] ?: User(),
                              isMine = appState.thisUser.value == skydive.userID,
                              onProfileClicked = {
                                  navController.navigate(Concept.Profile.route + skydive.userID)

                              },
                              onEditClicked = {
                                  viewModel.savedSkydives.skydive = skydive
                                  navController.navigate(Concept.Edit.route)
                              },
                          )
                      }
                  )
        },
        topBar = {
            AppTopAppBar(
                title = HOME_STRING
            )
        },
        bottomBar = {
            appState.thisUser.value?.let {
                AppBottomAppBar(navController = navController,
                    it
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (viewModel.checkPermissions(activity)) {
                        navController.navigate(Concept.TrackSkydive.route)
                    } else {
                        viewModel.requestPermissions(activity)
                    }
                },
                content = {
                    Icon(
                        painterResource(id = R.drawable.blue_plane),
                        contentDescription = ""
                    )
                },
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    )
}


