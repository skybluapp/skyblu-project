package com.skyblu.userinterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.skyblu.configuration.*
import com.skyblu.models.jump.bounds
import com.skyblu.models.jump.newest
import com.skyblu.models.jump.oldest
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.data.AppDataPoint2
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppTextField
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.ui.theme.ThemeBlueGradient
import com.skyblu.userinterface.viewmodels.TrackingScreenViewModel
import com.skyblu.userinterface.viewmodels.TrackingStatus
import com.skyblu.utilities.metersToFeet
import kotlin.math.roundToInt

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
                ActionConceptList(
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
                ActionConceptList(
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
                ActionConceptList(
                    menuActions = listOf(
                        ActionConcept(
                            action = {
                                viewModel.queueJump()
                                navController.navigate(Concept.Home.route)
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
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
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
                                points.bounds()?.let {
                                    CameraUpdateFactory
                                        .newLatLngBounds(
                                            it,
                                            5
                                        )
                                }?.let {
                                    cameraPositionState.animate(
                                        it,
                                    )
                                }
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
                .padding(SMALL_PADDING),
            verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        ) {
            AppTextField(
                value = state.title.value,
                onValueChanged = { state.title.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = TITLE_STRING,
                leadingIcon = R.drawable.number
            )
            AppTextField(
                value = state.dropzone.value,
                onValueChanged = { state.dropzone.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = DROPZONE_STRING,
                leadingIcon = R.drawable.location
            )
            AppTextField(
                value = state.aircraft.value,
                onValueChanged = { state.aircraft.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = AIRCRAFT_STRING,
                leadingIcon = R.drawable.aircraft
            )
            AppTextField(
                value = state.equipment.value,
                onValueChanged = { state.equipment.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = EQUIPMENT_STRING,
                leadingIcon = R.drawable.parachute
            )
            AppTextField(
                value = state.description.value,
                onValueChanged = { state.dropzone.value = it },
                imeAction = ImeAction.Next,
                onIme = { focusManager.moveFocus(FocusDirection.Down) },
                placeholder = DESCRIPTION_STRING,
                leadingIcon = R.drawable.edit
            )
        }
    }


    Scaffold(
        content = {
            if (viewModel.trackingStatus.value == TrackingStatus.TRACKING_COMPLETE) {
                TrackingCompleteContent()
            } else {
                ChiilledOutTrackingContent(viewModel.state.allPoints.value.lastOrNull()?.altitude)
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
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = false
    )
}

@Preview
@Composable
fun ChiilledOutTrackingContent(
    altitude : Float? = 4000f
){
    val typography = MaterialTheme.typography
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = ThemeBlueGradient),
        contentAlignment = Alignment.Center
    ){
        if(altitude != null){
            Text(
                text = altitude.metersToFeet().roundToInt().toString() +" ft",
                fontWeight = FontWeight.Bold,
                style = typography.h1
            )
        }


    }
}







