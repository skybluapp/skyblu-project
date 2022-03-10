package com.skyblu.skybluapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skyblu.skybluapp.ui.theme.SkyBluTheme
import com.skyblu.userinterface.screens.HomeScreen
import com.skyblu.userinterface.screens.MapScreen

class SkybluApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkyBluTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Navigator()
                }
            }
        }
    }
}

@Composable
fun Navigator(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "map"){
        composable("map"){ MapScreen { navController.navigate("home") }}
        composable("home"){ HomeScreen(navController = navController) }
    }
}