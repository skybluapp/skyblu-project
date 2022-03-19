package com.skyblu.userinterface.screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.skyblu.userinterface.componants.*

@Composable()
fun SettingsScreen(navController: NavController) {
    val navIcon = AppIcon.Settings



    Scaffold(
        content = {},
        topBar = {
            AppTopAppBar(
                title = navIcon.title,
                navigationIcon = {
                    MenuActionList(menuActions = listOf(
                        MenuAction(onClick = { navController.navigate("profile"){popUpTo("profile"){inclusive = true} } }, AppIcon.Previous)
                    ))
                },
                actionIcons = {
                    MenuActionList(menuActions = listOf(
                        MenuAction(onClick = { logout() }, AppIcon.Logout)
                    ))
                }
            )
        },
        bottomBar = {
            AppBottomAppBar(navController = navController)
        }
    )
}


//TODO
fun logout(){

}