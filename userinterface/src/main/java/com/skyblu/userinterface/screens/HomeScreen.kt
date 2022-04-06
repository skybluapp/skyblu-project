package com.skyblu.userinterface.screens

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyblu.models.jump.Skydive
import com.skyblu.models.jump.Skydiver
import com.skyblu.models.jump.generateSampleJumpList
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.viewmodels.HomeViewModel
import timber.log.Timber

//LazyColumn(Modifier.fillMaxSize()) {
//    items(state.items.size) { index ->
//        val skydive = state.items[index]
//        if (index >= state.items.size - 1 && !state.endReached && state.isLoading) {
//            viewModel.loadNextItems()
//        }
//        JumpCard(
//            skydive = skydive,
//            onMapClick = { navController.navigate(Concept.Map.route + "/" + skydive.skydiveID) }
//        )
//    }
//}
@Preview
@Composable
fun HomeScreen(
    list: List<Skydive> = generateSampleJumpList(),
    navController: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel(),
) {

    val state = viewModel.state
    val activity = LocalContext.current as Activity
    val users = viewModel.savedUsers.skydiverMap

    LaunchedEffect(
        key1 = viewModel.state.thisUser.value,
        block = {
            if (state.thisUser.value.isNullOrBlank()) {
                navController.navigate(Concept.LoggedOut.route)
            }
        }
    )


    val title : String = state.userMapping[state.thisUser.value]?.username ?: "???"

    Scaffold(
        content = {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.state.skydives.size) { index ->
                    val skydive = viewModel.state.skydives[index]
                    if(index >= viewModel.state.skydives.size - 1 && !state.endReached && !state.isLoading){
                        viewModel.loadNextSkydivePage()
                    }
                    Timber.d(skydive.skydiverID)
                    JumpCard(
                        skydive = skydive,
                        onMapClick = { navController.navigate(Concept.Map.route + "/" + skydive.skydiveID) },
                        username = viewModel.state.userMapping[skydive.skydiverID]?.username ?: "unknown",
                        skydiver =  viewModel.state.userMapping[skydive.skydiverID] ?: Skydiver()
                    )
                }
            }
        },
        topBar = {
            AppTopAppBar(
                title = title,
                navigationIcon = null,
                actionIcons = {
                    MenuActionList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.refresh() },
                                concept = Concept.Refresh
                            )
                        )
                    )
                }
            )
        },
        bottomBar = {
            AppBottomAppBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (viewModel.checkPermissions(activity)) {
                        Timber.d("Permission Granted")
                        navController.navigate(Concept.TrackSkydive.route)
                    } else {
                        Timber.d("Permission Not Granted")
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


