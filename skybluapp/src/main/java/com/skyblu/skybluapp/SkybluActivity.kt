package com.skyblu.skybluapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.skyblu.skybluapp.ui.theme.SkyBluTheme
import com.skyblu.userinterface.Navigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SkybluActivity @Inject constructor(
) : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkyBluTheme {
                    Navigation()
            }
        }
    }


}








