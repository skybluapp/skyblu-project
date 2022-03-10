package com.skyblu.userinterface.screens

import android.util.Log
import android.widget.Toolbar
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.skyblu.userinterface.componants.*

@Preview
@Composable
fun MapScreen(onClose : () -> Unit = {}){
    Log.d("hi", "hi")
    Scaffold(
        topBar = {
                 Toolbar(onPreviousClicked = { onClose() })
        },
        content = { JumpMap()}
    )
}

@Composable
fun Toolbar(onPreviousClicked : () ->Unit){
    AppTopAppBar(
        title = "",
        navigationIcon = {
            MenuActionList(
                listOf(
                    MenuAction(
                        menuIcon = MenuIcon.Previous,
                        onClick = {onPreviousClicked()}),
                ),
            )
        }
    )
}