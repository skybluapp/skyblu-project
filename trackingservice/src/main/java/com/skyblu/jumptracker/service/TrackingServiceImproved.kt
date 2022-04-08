package com.skyblu.jumptracker.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.skyblu.jumptracker.EmptyTrackingServiceCallbacks
import com.skyblu.jumptracker.TrackingServiceCallbacks
import com.skyblu.models.jump.SkydiveDataPoint
import com.skyblu.models.jump.SkydivePhase
import com.skyblu.trackingservice.R
import com.skyblu.utilities.hpaToMeters
import timber.log.Timber
import java.util.*
import kotlin.math.absoluteValue

/**
 * A service that dispatches instances of SkydiveDataPoint whenever either altitude or GPS thresholds are met
 */
class TrackingServiceImproved : Service(), SensorEventListener {

    private val binder = TrackingServiceBinder()

    /**
     * By default noting occurs when service actions occur
     */
    private var trackingServiceCallbacks: TrackingServiceCallbacks = EmptyTrackingServiceCallbacks()

    /**
     * Binder is passed to client. Client calls provideCallbacks to inform the service what should be done when service actions occur.
     */
    inner class TrackingServiceBinder : Binder() {

        /**
         * Client calls provideCallbacks to set what should be done when service actions are triggered
         * @param callbacks A class that implements TrackingServiceCallbacks functions, called when service actions are triggered
         * @see TrackingServiceCallbacks
         */
        fun provideCallbacks(callbacks: TrackingServiceCallbacks) {
            trackingServiceCallbacks = callbacks
        }
    }

    /**
     * Access sensor and location data
     */
    private lateinit var sensorManager: SensorManager
    private lateinit var locationClient: FusedLocationProviderClient

    //Holds values of last requested location & pressure
    private var locationsReceived: Int = 0
    private var pressuresRecieved: Int = 0
    private var mostRecentLocation: Location? = null
    private var mostRelevantLocation: Location? = null
    private var mostRecentAltitude: Float? = null
    private var mostRecentPressure: Float? = null
    private var mostRelevantAltitude: Float? = null
    private var altitudeUpdateTime: Long = System.currentTimeMillis()
    var mostRecentVerticalSpeed: Float? = null
    private var currentPhase = SkydivePhase.WALKING
    var currentPoint: SkydiveDataPoint? = null

    //Holds starting values of altitude and pressure
    var startAltitude: Float? = null
    private var jumpID = UUID.randomUUID().toString()

    //Holds the value of how frequently a new location is outputted
    private var refreshRate: Long = 1000
    fun setRefreshRate(refreshRate: Long) {
        this.refreshRate = refreshRate
        Timber.d("REFRESHRATE + $refreshRate")
    }

    // Holds weather the output loop has been stopped
    var isPaused: Boolean = false

    //Runs when service is created (Instantiates sensorManager and location client)
    override fun onCreate() {
        super.onCreate()
        Timber.d("TrackService Created")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Timber.d("TrackService Started")
        startForeground(
            1,
            createNotification()
        )
        return super.onStartCommand(
            intent,
            flags,
            startId
        )
    }

    //Runs once client is bound to service
    override fun onBind(intent: Intent?): IBinder {
        Timber.d("TrackService Bound")
        jumpID = UUID.randomUUID().toString()
        requestLocationUpdates()
        requestPressureUpdates()
        isPaused = false
        return binder
    }

    //Runs when a client unbinds
    override fun onUnbind(intent: Intent?): Boolean {
        isPaused = true
        sensorManager.unregisterListener(this)
        locationClient.removeLocationUpdates(locationCallback)
        stopSelf()
        return super.onUnbind(intent)
    }

    //Request to receive location updates
    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient.requestLocationUpdates(
                LocationRequest.create().setInterval(0)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            stopSelf()
        }
    }

    //Request updates from the pressure sensor
    private fun requestPressureUpdates() {
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.also { pressure ->
            sensorManager.registerListener(
                this,
                pressure,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    //Updates last location whenever a new location is recieved
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            Timber.d("Getting Location...")
            locationsReceived++
            if (mostRecentLocation == null || mostRelevantLocation == null) {
                mostRecentLocation = locationResult.lastLocation
                mostRelevantLocation = locationResult.lastLocation
            }
            val newLocation = locationResult.lastLocation
            mostRecentLocation = newLocation
            if (mostRecentLocation!!.distanceTo(mostRelevantLocation) > LOCATION_THRESHOLD || locationsReceived == 1) {
                mostRelevantLocation = newLocation
                Timber.d("Dispatch by location")
                dispatch()
            }
        }
    }

    //Updates last pressure whenever a new pressure is received
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)) {
            val newPressure = event?.values?.get(0)!!
            val newAltitude = newPressure.hpaToMeters()
            val currentTime = System.currentTimeMillis()

            pressuresRecieved++
            calculateVerticalSpeed(newAltitude)


            if (startAltitude == null || mostRecentAltitude == null || mostRelevantAltitude == null) {
                startAltitude = newAltitude
                mostRelevantAltitude = newAltitude
                mostRecentAltitude = newAltitude
                altitudeUpdateTime = currentTime
                mostRecentVerticalSpeed = 0f
                dispatch()
                return
            }

            if ((mostRelevantAltitude!! - newAltitude).absoluteValue > PRESSURE_THRESHOLD && currentTime > altitudeUpdateTime + 2000) {
                Timber.d("Getting Pressure...")

                altitudeUpdateTime = currentTime
                mostRelevantAltitude = newAltitude
                Timber.d("Dispatch by Altitude")

                dispatch()
            }

            mostRecentAltitude = newAltitude
            mostRecentPressure = newPressure
        }
    }

    fun dispatch() {
        if (mostRecentLocation == null) {
            Timber.d("Dispatch Ignored : No Recent Location")
            return
        }
        if (mostRecentAltitude == null) {
            Timber.d("Dispatch Ignored : No Recent Altitude")
            return
        }
        if (mostRecentPressure == null) {
            Timber.d("Dispatch Ignored : No Recent Pressure")
            return
        }
        Timber.d("Dispatch")
        if (currentPoint == null) {
            currentPoint = createMostRecentDataPoint(phase = SkydivePhase.WALKING)
            trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
            return
        }
        val newPoint = createMostRecentDataPoint()
        when (currentPoint!!.phase) {
            SkydivePhase.WALKING -> {
                Timber.d("Posting Walink")
                if (newPoint.groundSpeed > 20 && newPoint.altitude > 30) {
                    playTone()
                    currentPoint!!.phase = SkydivePhase.AIRCRAFT
                    trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
                    newPoint.phase = SkydivePhase.AIRCRAFT
                    currentPoint = newPoint
                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
                } else {
                    newPoint.phase = SkydivePhase.WALKING
                    currentPoint = newPoint
                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
                }
            }
            SkydivePhase.AIRCRAFT -> {
                Timber.d("Posting Aircraft")
                if (newPoint.verticalSpeed < -30 && newPoint.groundSpeed < 44) {
                    playTone()
                    currentPoint!!.phase = SkydivePhase.FREEFALL
                    trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
                    newPoint.phase = SkydivePhase.FREEFALL
                    currentPoint = newPoint
                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
                } else {
                    newPoint.phase = SkydivePhase.AIRCRAFT
                    currentPoint = newPoint
                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
                }
            }
            SkydivePhase.FREEFALL -> {
                Timber.d("Posting Freefall")
                if (newPoint.verticalSpeed > -30) {
                    playTone()
                    currentPoint!!.phase = SkydivePhase.CANOPY
                    trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
                    newPoint.phase = SkydivePhase.CANOPY
                    currentPoint = newPoint
                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
                } else {
                    newPoint.phase = SkydivePhase.FREEFALL
                    currentPoint = newPoint
                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
                }
            }
            SkydivePhase.CANOPY -> {
                Timber.d("Posting Canopy")
                if (newPoint.verticalSpeed > -0.1 && newPoint.altitude < startAltitude!! + 10) {
                    playTone()
                    currentPoint!!.phase = SkydivePhase.LANDED
                    trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
                    newPoint.phase = SkydivePhase.LANDED
                    currentPoint = newPoint
                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
                } else {
                    newPoint.phase = SkydivePhase.CANOPY
                    currentPoint = newPoint
                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
                }
            }
            SkydivePhase.LANDED -> {
                Timber.d("Posting Landed")
                newPoint.phase = SkydivePhase.LANDED
                currentPoint = newPoint
                trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("TrackService Destroyed")

        stopSelf()
    }

    fun createMostRecentDataPoint(phase: SkydivePhase = SkydivePhase.UNKNOWN): SkydiveDataPoint {
        return SkydiveDataPoint(
            dataPointID = UUID.randomUUID().toString(),
            jumpID = jumpID,
            latitude = mostRecentLocation!!.latitude,
            longitude = mostRecentLocation!!.longitude,
            timeStamp = mostRecentLocation!!.time,
            altitude = mostRecentAltitude!! - startAltitude!!,
            verticalSpeed = speed,
            groundSpeed = mostRecentLocation!!.speed,
            airPressure = mostRecentPressure!!,
            phase = phase
        )
    }

    // Creates notification required for foreground service
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String
    ): String {
        val chan = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun createNotification(): Notification {
        val channelId = createNotificationChannel(
            "TRACKING_SERVICE",
            "My Background Service"
        )
//        val pendingIntent: PendingIntent =
//            Intent(
//                this,
//                SkybluApp::class.java
//            ).let { notificationIntent ->
//                PendingIntent.getActivity(
//                    this,
//                    0,
//                    notificationIntent,
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            }
        return Notification.Builder(
            this,
            "TRACKING_SERVICE"
        )
            .setContentTitle("Tracking Service")
            .setContentText("Your jump is being tracked")
            .setSmallIcon(R.drawable.tracking)
//            .setContentIntent(pendingIntent)
            .setTicker("Tracking...")
            .build()
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }

    var lastSpeedCheck: Long = System.currentTimeMillis()
    var altitude1: Float? = null
    var speed : Float = 0F
    fun calculateVerticalSpeed(altitude2: Float) {
        if (altitude1 == null) {
            altitude1 = altitude2
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime > lastSpeedCheck + 5000){
            val speedo = (altitude2 - altitude1!!) / 5
            Timber.d("VERTICAL SPEED " + speedo.toString())
            speed = speedo
            lastSpeedCheck = currentTime
            altitude1 = altitude2
        }

    }
}



