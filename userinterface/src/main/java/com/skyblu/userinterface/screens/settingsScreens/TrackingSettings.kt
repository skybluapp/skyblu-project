package com.skyblu.userinterface.screens.settingsScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.settingsviewmodels.TrackingSettingsVm
import com.skyblu.userinterface.viewmodels.CreateAccountViewModel_Factory

@Composable()
fun TrackingSettingsScreen(
    navController: NavController,
    viewModel : TrackingSettingsVm = hiltViewModel()
) {

    val navIcon = Concept.Previous


    fun save() {
        viewModel.save()
    }

    Scaffold(
        content = {

            Column {
                AppSettingsSlider(
                    appConcepts = Concept.Plane,
                    title = "Aircraft Altitude Threshold",
                    prepend = "ft",
                    value = viewModel.aircraftAltitudeThreshold.value.toFloat(),
                    onValueChanged = { viewModel.aircraftAltitudeThreshold.value = it },
                    range = 30f .. 1000f
                )

                AppSettingsSlider(
                    appConcepts = Concept.Plane,
                    title = "Aircraft Certainty",
                    value = viewModel.aircraftCertainty.value.toFloat(),
                    onValueChanged = { viewModel.aircraftCertainty.value = it },
                    range = 5f .. 15f
                )
                AppSettingsSlider(
                    appConcepts = Concept.Parachute,
                    title = "Freefall Altitude Loss Threshold",
                    value = viewModel.aircraftCertainty.value.toFloat(),
                    onValueChanged = { viewModel.aircraftCertainty.value = it },
                    range = 50f .. 1000f
                )
                AppSettingsSlider(
                    appConcepts = Concept.Parachute,
                    title = "Freefall Speed Loss Threshold",
                    value = viewModel.aircraftCertainty.value.toFloat(),
                    onValueChanged = { viewModel.aircraftCertainty.value = it },
                    range = 10f .. 80f
                )
                AppSettingsSlider(
                    appConcepts = Concept.Parachute,
                    title = "Freefall Certainty",
                    value = viewModel.aircraftCertainty.value.toFloat(),
                    onValueChanged = { viewModel.aircraftCertainty.value = it },
                    range = 5f .. 15f
                )
                AppSettingsRangeSlider(
                    values = viewModel.canopyDetectionAltitude.value .. viewModel.freefallDetectionAltitude.value,
                    range = 800f .. 5000f,
                    title = "Freefall Range",
                    prepend = "m",
                    onValueChanged = { viewModel.canopyDetectionAltitude.value = it.start ; viewModel.freefallDetectionAltitude.value = it.endInclusive;}
                )

            }


        },
        topBar = {
            AppTopAppBar(
                title = "Tracking Settings",
                navigationIcon = {
                    MenuActionList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.navigate("settings") {
                                        popUpTo("settings") {
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
                                action = { save() },
                                concept = Concept.Save
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

