package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.viewmodels.LoginViewModel
import com.skyblu.userinterface.viewmodels.SettingsViewModel
import timber.log.Timber

val SETTINGS_LIST = listOf<Concept>(
    Concept.Account,
    Concept.LocationTracking,
    Concept.Mapping,
)

@Composable()
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    
    val settingsConcept = Concept.Settings
    val settingsList = settingsList(navController = navController)

    LaunchedEffect(
        key1 = viewModel.loggedIn.value,
        block = {
            Timber.d("Current User " + viewModel.currentUser())
            if(!viewModel.loggedIn.value){
                navController.navigate(Concept.LoggedOut.route)
            }
        }
    )


    Scaffold(
        content = {
            Column(Modifier.fillMaxSize()) {
                for (menuAction in settingsList) {
                    AppSettingsCategory(menuAction)
                }
            }
        },
        topBar = {
            AppTopAppBar(
                title = settingsConcept.title,
                navigationIcon = {
                    MenuActionList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.navigate(Concept.Profile.route) {
                                        popUpTo(Concept.Profile.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                concept = Concept.Previous
                            )
                        )
                    )
                },
                actionIcons = {
                    MenuActionList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.logout(); Timber.d("LOGOUT") },
                                concept = Concept.Logout
                            )
                        )
                    )
                },
            )
        },
        bottomBar = {
            AppBottomAppBar(navController = navController)
        }
    )
}

fun settingsList(navController: NavController): List<ActionConcept> {
    val settingsList: MutableList<ActionConcept> = mutableListOf()
    for (icon in SETTINGS_LIST) {
        settingsList.add(
            ActionConcept(
                action = { navController.navigate(icon.route + Concept.Settings.route) },
                concept = icon
            )
        )
    }
    return settingsList
}

fun logout() {

}