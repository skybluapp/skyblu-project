package com.skyblu.userinterface.screens

import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.skyblu.userinterface.componants.*


@Composable
fun MapScreen(navController: NavController){

    val navIcon = AppIcon.Map
    Scaffold(
        topBar = {

            AppTopAppBar(
                title = navIcon.title,
                navigationIcon = {
                    MenuActionList(menuActions = listOf(
                        MenuAction(onClick = { navController.navigate("home"){popUpTo("home"){inclusive = true} } }, AppIcon.Previous)
                    ))
                }
            )

        },
        content = { JumpMap()}
    )
}

