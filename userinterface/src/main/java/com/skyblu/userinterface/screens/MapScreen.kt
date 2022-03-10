package com.skyblu.userinterface.screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.skyblu.userinterface.componants.*

@Composable
fun MapScreen(onClose : () -> Unit){
    Scaffold(
        topBar = {

            Tool()

        },
        content = { JumpMap()}
    )
}

@Preview
@Composable
fun Tool(){
    AppTopAppBar(
        title = "",
        navigationIcon = {
            MenuActionList(
                listOf(
                    MenuAction(
                        menuIcon = MenuIcon.Previous,
                        onClick = {}),
                ),
            )
        }
    )
}