package com.skyblu.userinterface.screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.skyblu.userinterface.componants.*

@Composable()
fun ProfileScreen(navController: NavController) {
    val navIcon = AppIcon.Profile
    Scaffold(
        content = {},
        topBar = {
            AppTopAppBar(
                title = navIcon.title,
                actionIcons = {
                    MenuActionList(
                        appIcon = listOf(AppIcon.Settings),
                        navController = navController
                    )
                }
            )
        },
        bottomBar = {
            AppBottomAppBar(navController = navController)
        }
    )
}