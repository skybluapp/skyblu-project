package com.skyblu.jumptracker.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.media.AudioManager
import android.media.ToneGenerator
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

const val LOCATION_THRESHOLD = 0.1
const val PRESSURE_THRESHOLD = 0.1


/**
 * A service that dispatches instances of SkydiveDataPoint whenever either altitude or GPS thresholds are met
 */
class TrackingService : Service(), SensorEventListener  {


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
        fun provideCallbacks(callbacks : TrackingServiceCallbacks) {
            trackingServiceCallbacks = callbacks
        }
    }

    /**
     * Access sensor and location data
     */
    private lateinit var sensorManager: SensorManager
    private lateinit var locationClient: FusedLocationProviderClient

    //Holds values of last requested location & pressure
    private var recentLocation: Location = Location("")
    private var recentPressure = 0f
    private var recentAltitude = 0f
    private var currentPhase = SkydivePhase.WALKING


    //Holds starting values of altitude and pressure
    var baseAltitude: Float? = null
    private var basePressure: Float? = null
    private var jumpID = UUID.randomUUID().toString()

    var currentPoint : SkydiveDataPoint? = null


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
        startForeground(1, createNotification())
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
        OutputLooper().start()
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
            Timber.d("Permission Granted")
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

    //Updates last location whenever a new location is recieved
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            recentLocation = locationResult.lastLocation
            Timber.d("BANGO")
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

    //Updates last pressure whenever a new pressure is received
    override fun onSensorChanged(event: SensorEvent?) {
        var pressureNull = false
        if (baseAltitude == null || basePressure == null) {
            pressureNull = true
        }

        Timber.d("BINGO")

        if (event?.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)) {
            val pressure = event?.values?.get(0)!!
            val altitude = pressure.hpaToMeters()
            recentPressure = pressure
            recentAltitude = altitude
            if (pressureNull) {
                basePressure = pressure
                baseAltitude = altitude
            }
        }
    }


    inner class OutputLooper : Thread() {

        var lastAltitude: Float? = null
        override fun run() {
            while (!isPaused) {
                if (recentLocation.latitude != 0.0 || recentLocation.longitude != 0.0) {
                    if (lastAltitude != null) {
                        val altitudeChangePerSecond =
                            (recentAltitude - lastAltitude!!) * 1 / (refreshRate / 1000)

                        when(currentPhase){
                            SkydivePhase.WALKING -> {
                                if(recentLocation.speed > 20 && recentAltitude > 30){
                                    playTone()
                                    currentPhase = SkydivePhase.AIRCRAFT
                                    setRefreshRate(1000)
                                    currentPoint?.phase = SkydivePhase.AIRCRAFT
                                    currentPoint?.let { trackingServiceCallbacks.postSkydiveDataPoint(it) }

                                }
                            }
                            SkydivePhase.AIRCRAFT -> {
                                if(altitudeChangePerSecond < -30 && recentLocation.speed < 44){
                                    playTone()
                                    currentPhase = SkydivePhase.FREEFALL
                                    setRefreshRate(1000)
                                    currentPoint?.phase = SkydivePhase.FREEFALL
                                    currentPoint?.let { trackingServiceCallbacks.postSkydiveDataPoint(it) }
                                }
                            }
                            SkydivePhase.FREEFALL -> {
                                if(altitudeChangePerSecond > -30){
                                    playTone()
                                    currentPhase = SkydivePhase.CANOPY
                                    setRefreshRate(1000)
                                    currentPoint?.phase = SkydivePhase.CANOPY
                                    currentPoint?.let { trackingServiceCallbacks.postSkydiveDataPoint(it) }
                                }
                            }
                            SkydivePhase.CANOPY -> {
                                if(altitudeChangePerSecond > -0.1 && recentAltitude < baseAltitude!! + 10){
                                    playTone()
                                    currentPhase = SkydivePhase.LANDED
                                    setRefreshRate(1000)
                                    currentPoint?.phase = SkydivePhase.LANDED
                                    currentPoint?.let { trackingServiceCallbacks.postSkydiveDataPoint(it) }
                                }
                            }
                        }

                        currentPoint = SkydiveDataPoint(
                            airPressure = recentPressure,
                            altitude = recentAltitude,
                            latitude = recentLocation.latitude,
                            longitude = recentLocation.longitude,
                            timeStamp = System.currentTimeMillis(),
                            dataPointID = UUID.randomUUID().toString(),
                            groundSpeed = recentLocation.speed,
                            verticalSpeed = altitudeChangePerSecond,
                            phase = currentPhase,
                            jumpID = jumpID
                        )

                        trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)

                    }
                }
                lastAltitude = recentAltitude
                Thread.sleep(this@TrackingService.refreshRate)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("TrackService Destroyed")

        stopSelf()
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
}

fun playTone() {
    val tone = ToneGenerator(
        AudioManager.STREAM_MUSIC,
        100
    )
    tone.startTone(
        ToneGenerator.TONE_CDMA_ABBR_ALERT,
        150
    )
}

