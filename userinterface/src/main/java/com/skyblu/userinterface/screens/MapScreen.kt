package com.skyblu.userinterface.screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.viewmodels.MapViewModel

@Composable
fun MapScreen(
    navController: NavController,
    skydiveID : String,
    viewModel : MapViewModel = hiltViewModel(),
){
    val state = viewModel.state

    viewModel.setJump(skydiveID)

    Scaffold(
        topBar = {

            AppTopAppBar(
                title = Concept.Map.title,
                navigationIcon = {
                    MenuActionList(menuActions = listOf(
                        ActionConcept(action = { navController.navigate("home"){popUpTo("home"){inclusive = true} } },
                            concept = Concept.Previous
                        )
                    ))
                }
            )

        },
        content = {
            if(!state.isLoading.value){
                JumpMap(
                    points = viewModel.state.datapoints,
                    isLoading = state.isLoading.value
                )
            }

        }
    )
}

