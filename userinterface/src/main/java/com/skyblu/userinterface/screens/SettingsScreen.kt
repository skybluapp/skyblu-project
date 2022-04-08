package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyblu.configuration.Concept
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppSettingsCategory
import com.skyblu.userinterface.componants.scaffold.AppBottomAppBar
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.SettingsViewModel
import timber.log.Timber

val SETTINGS_LIST = listOf<Concept>(
    Concept.Account,
    Concept.LocationTracking,
    Concept.Mapping,
)

@Preview
@Composable()
fun SettingsScreen(
    navController: NavController = rememberNavController(),
    viewModel: SettingsViewModel = hiltViewModel()
) {
    
    val settingsConcept = Concept.Settings
    val settingsList = settingsList(navController = navController)

    LaunchedEffect(
        key1 = viewModel.loggedIn.value,
        block = {
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
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.navigate(Concept.Home.route) {
                                        popUpTo(Concept.Home.route) {
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
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.logout()},
                                concept = Concept.Logout
                            )
                        )
                    )
                },
            )
        },
        bottomBar = {
            viewModel.currentUser()?.let { AppBottomAppBar(navController = navController, userID = it) }
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