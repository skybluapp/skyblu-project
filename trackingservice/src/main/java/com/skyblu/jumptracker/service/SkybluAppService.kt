package com.skyblu.jumptracker.service

import android.Manifest
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.startForegroundService
import com.skyblu.jumptracker.TrackingServiceCallbacks
import com.skyblu.models.jump.SkydiveDataPoint
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Singleton that manages the flow of data between the service and activity the
 */
class SkybluAppService @Inject constructor(
    private val context: Context,
) : ClientToService {

    lateinit var trackingService: TrackingServiceImproved
    var isTrackingServiceBound: Boolean = false
    private var onRecieveTrackingPoint: (SkydiveDataPoint) -> Unit = {}

    /**
     * If set, service calls these functions when thresholds or criteria are met
     */
    private inner class Callbacks : TrackingServiceCallbacks {


        override fun postSkydiveDataPoint(dataPoint: SkydiveDataPoint) {
            onRecieveTrackingPoint(dataPoint)
        }

        override fun pressureSensorUnavailable() {
            Toast.makeText(
                context,
                "Pressure Sensor Unavailabe",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun locationUnavailable() {
            Toast.makeText(
                context,
                "Location Access Denied",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            val binder = service as TrackingServiceImproved.TrackingServiceBinder
            binder.provideCallbacks(Callbacks())
            isTrackingServiceBound = true
            Timber.d("Refresh Rate Service Bound" + this@SkybluAppService)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isTrackingServiceBound = false
        }
    }

    override fun setRefreshRate(refreshRate: Int) {
        if (isTrackingServiceBound) {
            trackingService.setRefreshRate(refreshRate = refreshRate.toLong())
        }
    }
    override fun getGroundAltitude(): Float? {
        return trackingService.startAltitude
    }

    override fun startTrackingService() {
        if (EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            if (!isTrackingServiceBound) {
                Timber.d("Intent to Start TrackService")


                Intent(
                    context,
                    TrackingServiceImproved::class.java
                ).also { intent ->
                    startForegroundService(
                        context,
                        intent
                    );

                    (context as Application).bindService(
                        intent,
                        connection,
                        Context.BIND_AUTO_CREATE
                    )
                }
            } else {
                Timber.d("TrackService already bound")
            }
        } else {
            Timber.d("TrackService Location Permission Is Required")
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    context as ComponentActivity,
                    1,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                    .setRationale("Location permission is required to track location during a jump")
                    .build(),
            )
        }
    }

    override fun stopTrackingService() {
        if (isTrackingServiceBound) {
            Timber.d("Intent to Unbind TrackService")
            (context as Application).unbindService(connection)
            isTrackingServiceBound = false
        } else {
            Timber.d("TrackService is not bound")
        }
    }

    override fun setOnRecieveSkydiveDataPoint(function: (SkydiveDataPoint) -> Unit) {
        onRecieveTrackingPoint = function
    }
}

interface ClientToService {
    fun setOnRecieveSkydiveDataPoint(function: (SkydiveDataPoint) -> Unit)
    fun stopTrackingService()
    fun startTrackingService()
    fun setRefreshRate(refreshRate: Int)
    fun getGroundAltitude() : Float?
}
