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
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.skyblu.jumptracker.MainActivity
import com.skyblu.jumptracker.TrackingServiceCallbacks
import com.skyblu.trackingservice.R
import com.skyblu.utilities.hpaToMeters
import timber.log.Timber

class TrackingService : Service(), SensorEventListener {



    //Interface provided from service to client so client can call service methods
    inner class TrackingServiceBinder : Binder(){
        fun getService() : TrackingService = this@TrackingService
    }
    private val binder = TrackingServiceBinder()

    //Interface provided from client to service so service can call client methods
    var trackingServiceCallbacks : TrackingServiceCallbacks? = null

    //Manages device sensors, in this case Pressure sensor
    private lateinit var sensorManager : SensorManager

    //Recieves Location Updates
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Holds values of last requested location & pressure
    private var lastLocation : Location = Location("")
    private var lastPressure = 0f
    private var lastAltitude = 0f
    private var lastAccelerometer = arrayOf(0f, 0f, 0f)

    //Holds the value of how frequently a new location is outputted
    private var refreshRate : Long = 500
    fun setRefreshRate(refreshRate : Long){
        this.refreshRate = refreshRate
    }

    // Holds weather the output loop has been stopped
    var isPaused : Boolean = false

    //Runs when service is created (Instantiates sensorManager and location client)
    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    //Runs once client is bound to service
    override fun onBind(intent: Intent?): IBinder {
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
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopSelf()
        return super.onUnbind(intent)
    }

    //Request to receive location updates
    private fun requestLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(LocationRequest.create().setInterval(0).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY), locationCallback, Looper.getMainLooper())
        } else {
            stopSelf()
        }
    }

    //Updates last location whenever a new location is recieved
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            lastLocation = locationResult.lastLocation
        }
    }

    //Request updates from the pressure sensor
    private fun requestPressureUpdates(){
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.also { pressure -> sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_FASTEST) }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerate -> sensorManager.registerListener(this, accelerate, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    //Updates last pressure whenever a new pressure is received
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)){
            lastPressure = event?.values?.get(0)!!
            lastAltitude = lastPressure.hpaToMeters()
        }
        if(event?.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)){
            lastAccelerometer[0] = event?.values?.get(0)!!
            lastAccelerometer[1] = event.values?.get(1)!!
            lastAccelerometer[2] = event.values?.get(2)!!
        }

    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    inner class OutputLooper : Thread() {
        override fun run() {
            while(!isPaused){
                val timeStamp = System.currentTimeMillis()
                if(lastLocation.latitude != 0.0 || lastLocation.longitude != 0.0){
                    trackingServiceCallbacks?.postLocation(lastLocation, lastPressure, lastAltitude, timeStamp)
                }
                Thread.sleep(this@TrackingService.refreshRate)
            }
        }
    }

    
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("TrackService Destroyed")

        stopSelf()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("TrackService Started")
        startForeground(1, createNotification())
        return super.onStartCommand(intent, flags, startId)
    }
    // Creates notification required for foreground service
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
    private fun createNotification(): Notification {
        val channelId = createNotificationChannel("TRACKING_SERVICE", "My Background Service")
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        return Notification.Builder(this, "TRACKING_SERVICE")
            .setContentTitle("Tracking Service")
            .setContentText("Your jump is being tracked")
            .setSmallIcon(R.drawable.tracking)
            .setContentIntent(pendingIntent)
            .setTicker("Tracking...")
            .build()
    }
}

