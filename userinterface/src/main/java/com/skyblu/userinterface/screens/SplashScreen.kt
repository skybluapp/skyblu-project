package com.skyblu.uicomponants.componants

import android.view.animation.OvershootInterpolator
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skyblu.userinterface.R
import com.skyblu.userinterface.ui.theme.ThemeBlueGradient
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun SplashScreen(navController : NavController){

    LaunchedEffect(key1 = true) {

        delay(1400)
        Timber.d("sss")
        navController.navigate("loggedIn")
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = ThemeBlueGradient),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.aircraft),
            contentDescription = "Skyblu Logo",
            alignment = Alignment.Center,
            modifier = Modifier.size(200.dp),
        )
    }
}



