package com.skyblu.trackingservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.skyblu.TrackingServiceViewModel
import com.skyblu.trackingservice.service.JumpTrackerService

const val TAG = "Main Activity"

class TrackingServiceActivity : ComponentActivity() {

    private var jumpTrackerService : JumpTrackerService? = null
    private var isJumpTrackerServiceBound : Boolean = false

    private val mainActivityViewModel : TrackingServiceViewModel by viewModels()


    //UI Data
    private val count = 0
    private val maxCount = 0



    private val connection = object : ServiceConnection{

        override fun onServiceConnected(className: ComponentName?, service: IBinder) {
            val binder = service as JumpTrackerService.JumpTrackerBinder
            jumpTrackerService = service.getService()
            isJumpTrackerServiceBound = true
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            isJumpTrackerServiceBound = false
        }

    }

    override fun onStart() {
        super.onStart()
        Intent(this, JumpTrackerService::class.java).also {intent->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isJumpTrackerServiceBound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme() {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }

        val observer = object : Observer<JumpTrackerService.JumpTrackerBinder> {
            override fun onChanged(t: JumpTrackerService.JumpTrackerBinder?) {
                if(t != null){
                    Log.d(TAG, "onChanged: connected to service")
                    jumpTrackerService = t.getService()
                } else {
                    Log.d(TAG, "onChanged: unbound from service")
                    jumpTrackerService = null
                }
            }

        }

        var progress : Int = 0

        val observer2 = object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if(t == true){
                    if(mainActivityViewModel.getMyBinder().value != null){
                        if(jumpTrackerService!!.getCount() == jumpTrackerService!!.getMaxValue()){
                            mainActivityViewModel.setIsProgressUpdating(false)
                            progress = jumpTrackerService!!.getCount()
                        }
                    } else {

                    }
                }
            }
        }



        mainActivityViewModel.getIsProgressUpdating().observe(this, observer2)
        mainActivityViewModel.getMyBinder().observe(this, observer)
    }

    fun toggleUpdates(){
        if(jumpTrackerService != null){
            if(jumpTrackerService!!.getCount() == jumpTrackerService!!.getMaxValue()){
                jumpTrackerService!!.resetCount()
            } else {
                if(jumpTrackerService!!.getIsPaused()){
                    jumpTrackerService!!.unpauseTracking()
                    mainActivityViewModel.setIsProgressUpdating(true)
                } else {
//                    jumpTrackerService!!.pauseTracking()
                    mainActivityViewModel.setIsProgressUpdating(false)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        startService()
    }

    fun startService(){
        val serviceIntent : Intent = Intent(this, JumpTrackerService::class.java)
        startService(serviceIntent)
        bindService()
    }

    fun bindService(){
        val serviceIntent : Intent = Intent(this, JumpTrackerService::class.java)
        bindService(serviceIntent, mainActivityViewModel.getServiceConnection() , Context.BIND_AUTO_CREATE)
    }

    fun getServiceConnection() : ServiceConnection{
        return connection
    }

    override fun onPause() {
        super.onPause()
        if(mainActivityViewModel.getMyBinder() != null){
            unbindService(mainActivityViewModel.getServiceConnection())
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}

@Preview(showSystemUi = true)
@Composable
fun JumpTrackerServiceScreen(){
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Column(verticalArrangement = Arrangement.spacedBy(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            ListItem(label = "Pings")
            ListItem(label = "Lat")
            ListItem(label = "Long")
            ListItem(label = "Alt")
            ServiceControls()
        }
    }

}

@Preview
@Composable
fun ListItem(label : String = "Last Latitude", value : String = "10.59283"){
    Row() {
        Text(
            modifier = Modifier.padding(end = 10.dp),
            text = label,
            fontWeight = FontWeight.Bold
        )
        Text(text = value)
    }
}

@Preview
@Composable
fun ServiceControls(startTracking : () -> Unit = {}, stopTracking : () -> Unit = {}){
    Row() {
        Button(
            onClick = {startTracking()},
            modifier = Modifier.padding(end = 10.dp),
        ) {
            Text(text = "Start Tracking", fontWeight = FontWeight.Bold)
        }
        Button(onClick = { stopTracking()}) {
            Text(text = "Stop Tracking", fontWeight = FontWeight.Bold)
        }
    }
}