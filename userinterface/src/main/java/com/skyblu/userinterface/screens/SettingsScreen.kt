package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyblu.configuration.Concept
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppSettingsCategory
import com.skyblu.userinterface.componants.scaffold.AppBottomAppBar
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.SettingsViewModel

val SETTINGS_LIST = listOf<Concept>(
    Concept.Account,
    Concept.LocationTracking,
    Concept.Mapping,
)

val TRACKING_SETTINGS_LIST = listOf<Concept>(
    Concept.AircraftThreshold,
    Concept.FreefallThreshold,
    Concept.CanopyThreshold
)

enum class SettingsScreen {
    ROOT,
    TRACK,
    MAPPING,
}

@Composable()
fun SettingsScreen(
    navController: NavController = rememberNavController(),
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val state = viewModel.state

    val settingsConcept = Concept.Settings
    val settingsList = settingsList(navController = navController)
    val trackingSettingsList = settingsList(navController = navController)

    LaunchedEffect(
        key1 = viewModel.loggedIn.value,
        block = {
            if (!viewModel.loggedIn.value) {
                navController.navigate(Concept.LoggedOut.route)
            }
        }
    )

    @Composable
    fun root() {
        Column(Modifier.fillMaxSize()) {
            for (menuAction in settingsList) {
                AppSettingsCategory(menuAction)
            }
        }
    }

    @Composable
    fun tracking() {
        Column(Modifier.fillMaxSize()) {
            for (menuAction in trackingSettingsList) {
                AppSettingsCategory(menuAction)
            }
        }
    }

//    val content  = when(state.screen.value){
//        SettingsScreen.TRACK -> { _ ->
//
//        }
//        SettingsScreen.ROOT -> { _ ->
//
//        }
//
//        SettingsScreen.MAPPING -> { _ ->
//
//        }
//    }
//

    Scaffold(
        content ={},
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
                                action = { viewModel.logout() },
                                concept = Concept.Logout
                            )
                        )
                    )
                },
            )
        },
        bottomBar = {
            viewModel.currentUser()?.let {
                AppBottomAppBar(
                    navController = navController,
                    userID = it
                )
            }
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

@Preview(showBackground = true)
@Composable
fun p() {
    Button(onClick = { /*TODO*/ }) {

    }
}