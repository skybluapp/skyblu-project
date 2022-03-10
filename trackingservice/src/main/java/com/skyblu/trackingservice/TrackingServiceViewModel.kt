package com.skyblu

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyblu.trackingservice.service.JumpTrackerService

class TrackingServiceViewModel : ViewModel() {
    val TAG : String = "MainActivityViewModel"

    private var mIsProgressUpdating : MutableLiveData<Boolean> = MutableLiveData()
    private val mBinder : MutableLiveData<JumpTrackerService.JumpTrackerBinder> = MutableLiveData()

    private val connection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d(TAG, "Connected to service")
            val binder : JumpTrackerService.JumpTrackerBinder = p1 as JumpTrackerService.JumpTrackerBinder
            mBinder.postValue(binder)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mBinder.postValue(null)
        }

    }


    fun getIsProgressUpdating() : LiveData<Boolean>{
        return mIsProgressUpdating
    }

    fun getMyBinder() : LiveData<JumpTrackerService.JumpTrackerBinder>{
        return mBinder
    }

    fun getServiceConnection() : ServiceConnection{
        return connection
    }

    fun setIsProgressUpdating(isUpdating : Boolean){
        mIsProgressUpdating.postValue(isUpdating)
    }
}