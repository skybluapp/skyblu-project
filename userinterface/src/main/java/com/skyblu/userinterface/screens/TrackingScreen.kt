package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.skyblu.models.jump.bounds
import com.skyblu.models.jump.newest
import com.skyblu.models.jump.oldest
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.viewmodels.TrackingScreenViewModel
import com.skyblu.userinterface.viewmodels.TrackingStatus

@Composable
fun TrackingScreen(
    navController: NavController,
    viewModel: TrackingScreenViewModel = hiltViewModel(),
    ) {

    val navIcon = Concept.TrackSkydive

    @Composable
    fun TrackingTopBar() {
        AppTopAppBar(
            title = viewModel.state.allPoints.value.size.toString(),
            navigationIcon = {
                MenuActionList(
                    menuActions = listOf(
                        ActionConcept(
                            action = {
                                viewModel.stopTracking()
                                navController.navigate(Concept.Home.route) {
                                    popUpTo(Concept.Home.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            concept = Concept.Close
                        )
                    )
                )
            },
        )
    }

    @Composable
    fun TrackingCompleteTopBar() {
        AppTopAppBar(
            title = navIcon.title,
            navigationIcon = {
                MenuActionList(
                    menuActions = listOf(
                        ActionConcept(
                            action = {
                                viewModel.trackingStatus.value = TrackingStatus.NOT_TRACKING
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
                            action = {
                                viewModel.queueJump()
                                navController.navigate("home")
                            },
                            concept = Concept.Save
                        )
                    )
                )
            },
        )
    }

    @Composable
    fun NotTrackingFab() {
        ExtendedFloatingActionButton(
            text = { Text(text = "Start Tracking") },
            onClick = { viewModel.onStartTrackingClicked() },
            icon = {
                Icon(
                    painterResource(id = R.drawable.blue_plane),
                    contentDescription = ""
                )
            }
        )
    }

    @Composable
    fun TrackingFab() {
        ExtendedFloatingActionButton(
            text = { Text(text = "Stop Tracking") },
            onClick = { viewModel.onStopTrackingService() },
            icon = {
                Icon(
                    painterResource(id = R.drawable.blue_plane),
                    contentDescription = ""
                )
            },
            backgroundColor = Color.Red
        )
    }

    @Composable
    fun TrackingContent() {
        val state = viewModel.state
        val points = state.allPoints.value
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            ) {
                val cameraPositionState: CameraPositionState = rememberCameraPositionState()
                if (state.allPoints.value.isNotEmpty()) {
                    LaunchedEffect(
                        key1 = points.newest(),
                        block = {
                            if (!cameraPositionState.isMoving) {
                                cameraPositionState.animate(
                                    CameraUpdateFactory
                                        .newLatLngBounds(
                                            points.bounds(),
                                            5
                                        ),
                                )
                            }
                        }
                    )
                }
                LiveJumpMap(
                    cameraPositionState = cameraPositionState,
                    points = points
                )
            }
            Row() {
                Column(Modifier.fillMaxWidth(0.5f)) {
                    AppDataPoint2(
                        Concept.Latitude,
                        data = points.newest()?.latitude.toString()
                    )
                }
                Column(Modifier.fillMaxWidth()) {
                    AppDataPoint2(
                        Concept.Longitude,
                        data = points.newest()?.longitude.toString()
                    )
                }
            }
            Row() {
                Column(Modifier.fillMaxWidth(0.5f)) {
                    AppDataPoint2(
                        Concept.GroundAirPressure,
                        data = points.oldest()?.airPressure.toString()
                    )
                }
                Column(Modifier.fillMaxWidth()) {
                    AppDataPoint2(
                        Concept.AirPressure,
                        data = points.newest()?.airPressure.toString()
                    )
                }
            }
            Row() {
                Column(Modifier.fillMaxWidth(0.5f)) {
                    AppDataPoint2(
                        Concept.BaseAltitude,
                        data = points.oldest()?.altitude.toString()
                    )
                }
                Column(Modifier.fillMaxWidth()) {
                    AppDataPoint2(
                        Concept.Altitude,
                        data = points.newest()?.altitude.toString()
                    )
                }
            }
            Row() {
                Column(Modifier.fillMaxWidth(0.5f)) {
                    AppDataPoint2(
                        Concept.JumpStatus,
                        data = points.newest()?.phase.toString()
                    )
                }
                Column(Modifier.fillMaxWidth()) {
                    AppDataPoint2(
                        Concept.Altitude,
                        data = points.newest()?.altitude.toString()
                    )
                }
            }
        }
    }

    @Composable
    fun TrackingCompleteContent() {
        val focusManager = LocalFocusManager.current
        val state = viewModel.state
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AppTextField(
                value = state.jumpTitle.value,
                onValueChanged = { state.jumpTitle.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = "Jump Title",
                leadingIcon = R.drawable.number
            )
            AppTextField(
                value = state.jumpDropzone.value,
                onValueChanged = { state.jumpDropzone.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = "Dropzone",
                leadingIcon = R.drawable.location
            )
            AppTextField(
                value = state.jumpAircraft.value,
                onValueChanged = { state.jumpAircraft.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = "Aircraft",
                leadingIcon = R.drawable.aircraft
            )
            AppTextField(
                value = state.jumpEquipment.value,
                onValueChanged = { state.jumpEquipment.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = "Equipment",
                leadingIcon = R.drawable.parachute
            )
            AppTextField(
                value = state.jumpDescription.value,
                onValueChanged = { state.jumpDescription.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = "Description",
                leadingIcon = R.drawable.edit
            )
        }
    }


    Scaffold(
        content = {
            if (viewModel.trackingStatus.value == TrackingStatus.TRACKING_COMPLETE) {
                TrackingCompleteContent()
            } else {
                TrackingContent()
            }
        },
        topBar = {
            if (viewModel.trackingStatus.value == TrackingStatus.TRACKING_COMPLETE) {
                TrackingCompleteTopBar()
            } else {
                TrackingTopBar()
            }
        },
        floatingActionButton = {
            when (viewModel.trackingStatus.value) {
                TrackingStatus.NOT_TRACKING -> {
                    NotTrackingFab()
                }
                TrackingStatus.TRACKING -> {
                    TrackingFab()
                }
                else -> {}
            }
        }
    )
}


