package com.skyblu.skybluapp

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import com.skyblu.userinterface.viewmodels.TrackingScreenViewModel
import com.skyblu.jumptracker.TrackingServiceCallbacks
import com.skyblu.jumptracker.service.TrackingService
import com.skyblu.models.jump.TrackingPoint
import com.skyblu.skybluapp.ui.theme.SkyBluTheme
import com.skyblu.uicomponants.componants.SplashScreen
import com.skyblu.userinterface.componants.AppIcon
import com.skyblu.userinterface.screens.*
import com.skyblu.userinterface.wrappers.HomeScreenWrapper
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber
import java.util.*

class SkybluApp : ComponentActivity(), EasyPermissions.PermissionCallbacks {

    lateinit var trackingService: TrackingService
    var isTrackingServiceBound : Boolean = false

    private val viewModel : TrackingScreenViewModel by viewModels()


    inner class Callbacks : TrackingServiceCallbacks{
        override fun postLocation(
            location: Location,
            pressure: Float,
            altitude: Float,
            timeStamp: Long
        ) {
            val trackingPoint = TrackingPoint(airPressure = pressure, altitude = altitude, timeStamp = timeStamp, latitude = location.latitude, longitude = location.longitude, trackingPointId = UUID.randomUUID().toString())
            viewModel.receiveTrackingPoint(trackingPoint)
        }
        override fun pressureSensorUnavailable() {
            Toast.makeText(this@SkybluApp, "Pressure Sensor Unavailabe", Toast.LENGTH_SHORT).show()

        }
        override fun locationUnavailable() {
            Toast.makeText(this@SkybluApp, "Location Access Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.d("TrackService Connected")
            val binder = service as TrackingService.TrackingServiceBinder
            trackingService = binder.getService()
            trackingService.trackingServiceCallbacks = Callbacks()
            isTrackingServiceBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("TrackService Disconnected")
            isTrackingServiceBound = false
        }
    }

    private fun startTrackingService(){

        if(EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            if(!isTrackingServiceBound){
                Timber.d("Intent to Start TrackService")
                Intent(this, TrackingService::class.java).also { intent -> startForegroundService(intent); bindService(intent, connection, Context.BIND_AUTO_CREATE) }
            } else {
                Timber.d("TrackService already bound")
            }
        } else {
            Timber.d("TrackService Location Permission Is Required")
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, 1, Manifest.permission.ACCESS_FINE_LOCATION)
                    .setRationale("Location permission is required to track location during a jump")
                    .build(),
            )
        }
    }

    private fun stopTrackingService(){
        if(isTrackingServiceBound){
            Timber.d("Intent to Unbind TrackService")
            unbindService(connection)
            isTrackingServiceBound = false
        } else {
            Timber.d("TrackService is not bound")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkyBluTheme {

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Navigator(checkPermissions = ::checkPermission, requestPermissions = ::requestPermission, startTrackingService = {startTrackingService()}, viewModel)
                }
            }
        }


    }



    override fun onPermissionsGranted(
        requestCode: Int,
        perms: MutableList<String>
    ) {
        TODO("Not yet implemented")
    }
    override fun onPermissionsDenied(
        requestCode: Int,
        perms: MutableList<String>
    ) {
        TODO("Not yet implemented")
    }

    fun checkPermission(vararg permissions : String) : Boolean{
        return EasyPermissions.hasPermissions(applicationContext, *permissions)
    }

    fun requestPermission(vararg permissions : String) {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(this, 1, *permissions)
                .build(),
        )
    }



}

@Composable
fun Navigator(
    checkPermissions: (String) -> Boolean,
    requestPermissions: (String) -> Unit,
    startTrackingService: () -> Unit,
    viewModel: TrackingScreenViewModel
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash"){
        composable("splash"){ SplashScreen(navController = navController)}
        navigation(route = "loggedIn" , startDestination = "home"){
            composable(AppIcon.Login.route){ MapScreen(navController = navController)}
            composable("home"){ HomeScreenWrapper(
                navController = navController,
                checkPermission = checkPermissions,
                requestPermission = requestPermissions
            ) }
            composable("profile"){ ProfileScreen(navController = navController) }
            composable("settings"){ SettingsScreen(navController = navController) }
            composable("map"){ MapScreen(navController = navController) }
            composable("track"){ TrackingScreen(navController = navController, onStartTrackingClicked = {startTrackingService()}, viewModel) }
        }
        navigation(route = "loggedOut" , startDestination = "welcome"){
            composable("welcome"){}
        }
    }
}








