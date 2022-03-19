package com.skyblu.userinterface.wrappers

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.skyblu.userinterface.componants.AppIcon
import com.skyblu.userinterface.screens.HomeScreen
import timber.log.Timber

@Composable
fun HomeScreenWrapper(
    navController: NavController,
    checkPermission : (String) -> Boolean,
    requestPermission : (String) -> Unit
){



    fun onFabClicked(){
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
            Timber.d("Permission Granted")
            navController.navigate(AppIcon.TrackSkydive.route)
        } else {
            Timber.d("Permission Not Granted")
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    HomeScreen(
        onFabClicked = {onFabClicked()},
        navController = navController,

    )
}