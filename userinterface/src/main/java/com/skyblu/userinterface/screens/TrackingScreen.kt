package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.viewmodels.TrackingScreenViewModel
import com.skyblu.utilities.metersToFeet

@Composable
fun TrackingScreen(
    navController: NavController,
    onStartTrackingClicked: () -> Unit = {},
    viewModel: TrackingScreenViewModel
) {
    val trackingdata = viewModel.trackingData
    val navIcon = AppIcon.TrackSkydive





    Scaffold(
        content = {
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                ) {
                    val cameraPositionState: CameraPositionState = rememberCameraPositionState()
                    if (trackingdata.walkingTrackingPoints.isNotEmpty()) {
                        LaunchedEffect(
                            key1 = trackingdata.allTrackingPoints(),
                            block = {
                                if (!cameraPositionState.isMoving) {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory
                                            .newLatLngBounds(
                                                trackingdata.getCameraBounds(),
                                                5
                                            ),
                                    )
                                }
                            }
                        )
                    }
                    LiveJumpMap(
                        cameraPositionState = cameraPositionState,
                        walkingPoints = trackingdata.walkingTrackingPoints,
                        aircraftPoints = trackingdata.aircraftTrackingPoints,
                        freefallPoints = trackingdata.freefallTrackingPoints,
                        canopyPoints = trackingdata.canopyTrackingPoints,
                        landedPoints = trackingdata.landedTrackingPoints,
                    )
                }
                Row() {
                    Column(Modifier.fillMaxWidth(0.5f)) {
                        AppDataPoint2(
                            AppIcon.Latitude,
                            data = trackingdata.getLastTrackingPoint()?.latitude.toString()
                        )
                    }
                    Column(Modifier.fillMaxWidth()) {
                        AppDataPoint2(
                            AppIcon.Longitude,
                            data = trackingdata.getLastTrackingPoint()?.latitude.toString()
                        )
                    }
                }
                Row() {
                    Column(Modifier.fillMaxWidth(0.5f)) {
                        AppDataPoint2(
                            AppIcon.GroundAirPressure,
                            data = trackingdata.getFirstTrackingPoint()?.airPressure.toString()
                        )
                    }
                    Column(Modifier.fillMaxWidth()) {
                        AppDataPoint2(
                            AppIcon.AirPressure,
                            data = trackingdata.getLastTrackingPoint()?.airPressure.toString()
                        )
                    }
                }
                Row() {
                    Column(Modifier.fillMaxWidth(0.5f)) {
                        AppDataPoint2(
                            AppIcon.BaseAltitude,
                            data = String.format("%.2f", viewModel.baseAltitude.value)  + "m"
                        )
                    }
                    Column(Modifier.fillMaxWidth()) {
                        AppDataPoint2(
                            AppIcon.Altitude,
                            data = String.format("%.2f", viewModel.altitude.value)  + "m"
                        )
                    }
                }
                Row() {
                    Column(Modifier.fillMaxWidth(0.5f)) {
                        AppDataPoint2(
                            AppIcon.TotalDistance,
                            data = String.format("%.2f", viewModel.totalDistance.value)  + "m"
                        )
                    }
                    Column(Modifier.fillMaxWidth()) {
                        AppDataPoint2(
                            AppIcon.SectorDistance,
                            data = String.format("%.2f", viewModel.sectorDistance.value)  + "m"
                        )
                    }
                }
                Row() {
                    Column(Modifier.fillMaxWidth(0.5f)) {
                        AppDataPoint2(
                            AppIcon.PointsAccepted,
                            data = viewModel.pointsAccepted.value.toString()
                        )
                    }
                    Column(Modifier.fillMaxWidth()) {
                        AppDataPoint2(
                            AppIcon.PointsRejectd,
                            data = viewModel.pointsRejected.value.toString()
                        )
                    }
                }
                Row() {
                    Column(Modifier.fillMaxWidth(0.5f)) {
                        AppDataPoint2(
                            AppIcon.JumpStatus,
                            data = viewModel.altitudeStatus.value.label
                        )
                    }
                    Column(Modifier.fillMaxWidth()) {
                        AppDataPoint2(
                            AppIcon.JumpStatus,
                            data = viewModel.jumpStatus.value.label
                        )
                    }
                }
            }
        },
        topBar = {
            AppTopAppBar(
                title = navIcon.title,
                navigationIcon = {
                    MenuActionList(
                        menuActions = listOf(
                            MenuAction(
                                onClick = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                },
                                AppIcon.Close
                            )
                        )
                    )
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Start Tracking") },
                onClick = { onStartTrackingClicked() },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.blue_plane),
                        contentDescription = ""
                    )
                }
            )
        }
    )
}
//
//@Composable
//@Preview
//fun TrackingScreenPreview(){
//    TrackingScreen(
//        navController = rememberNavController(),
//        viewModel = com.skyblu.skybluapp.viewmodels.MainViewModel()
//    )
//}