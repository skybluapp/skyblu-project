package com.skyblu.jumptracker

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
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.skyblu.jumptracker.service.TrackingService
import com.skyblu.jumptracker.ui.theme.JumpTrackerTheme
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber

class MainActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks {

    lateinit var trackingService: TrackingService
    var isTrackingServiceBound : Boolean = false
//    private val viewModel : MainViewModel by viewModels()

    inner class Callbacks : TrackingServiceCallbacks{
        override fun postLocation(
            location: Location,
            pressure: Float,
            altitudeFt: Float,
            timeStamp: Long
        ) {

//            viewModel.recieveLocation(location = location, pressure = pressure, accelerometer = accelerometer)
        }
        override fun pressureSensorUnavailable() {
            Toast.makeText(this@MainActivity, "Pressure Sensor Unavailabe", Toast.LENGTH_SHORT).show()

        }
        override fun locationUnavailable() {
            Toast.makeText(this@MainActivity, "Location Access Denied", Toast.LENGTH_SHORT).show()
        }
    }



    private val connection = object : ServiceConnection{
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JumpTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Timber.d("Content Set")
//                    JumpTrackerServiceScreen(startService = { startTrackingService() }, stopService = { stopTrackingService() }, function = {invokeServiceFunction()}, viewModel = viewModel)

                }
            }
        }
    }

    private fun invokeServiceFunction(){
//        if(isTrackingServiceBound){
//            val num: Int = trackingService.count
//            Timber.d( "TrackService vm lat is %s", viewModel.recentLocation.value.latitude.toString())
//        } else {
//            Timber.d("Cannot invoke: TrackService is not bound")
//        }
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



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.d("Location permission granted")
        if(requestCode == 1){
            startTrackingService()
        }
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Timber.d("Location permission denied")
    }
}

