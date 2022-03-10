package com.skyblu.trackingservice.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleService
import java.util.*
import kotlin.math.max

class JumpTrackerService : Service() {

    private val TAG : String = "JumpTrackerService"
    private var mBinder : IBinder = JumpTrackerBinder()
    private lateinit var handler : Handler
    private var count = 0
    private val maxCount = 100
    private var isPaused = true
    private val looper : MainLoop = MainLoop()


    inner class MainLoop : Runnable{
        override fun run() {
            if(count >= maxCount || isPaused){
                handler.removeCallbacks(this)
                pauseTracking()
            } else {
                Log.d(TAG, "count is ${count.toString()}")
                count++
                handler.postDelayed(this, 100)
            }
        }
    }

    fun pauseTracking() {
        isPaused = true
    }

    fun unpauseTracking(){
        isPaused = false
        startTracking()
    }

    fun getIsPaused() : Boolean{
        return isPaused
    }

    fun getCount() : Int{
        return count
    }

    fun getMaxValue() : Int{
        return maxCount
    }

    fun resetCount(){
        count = 0
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    inner class JumpTrackerBinder : Binder(){
        fun getService() : JumpTrackerService{
            return this@JumpTrackerService
        }
    }

    override fun onCreate() {
        handler = Handler(Looper.getMainLooper())
        isPaused = true
    }

    fun startTracking(){
        handler.postDelayed(looper, 100)
    }
}