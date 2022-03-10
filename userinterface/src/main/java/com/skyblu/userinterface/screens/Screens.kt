package com.skyblu.uicomponants.componants

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyblu.userinterface.R
import com.skyblu.userinterface.ui.theme.ThemeBlueGradient

@Preview
@Composable
fun SplashScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = ThemeBlueGradient),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.plane),
            contentDescription = "Skyblu Logo",
            alignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        )
    }
}



