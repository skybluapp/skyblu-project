package com.skyblu.userinterface.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.generateSampleJump
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.AppBottomAppBar
import com.skyblu.userinterface.componants.AppTopAppBar
import com.skyblu.userinterface.componants.JumpCard

@Preview
@Composable
fun HomeScreen(
    list: List<Jump> = listOf(
        generateSampleJump(), generateSampleJump(),
        generateSampleJump(), generateSampleJump(),
        generateSampleJump() , generateSampleJump(),
        generateSampleJump(), generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),generateSampleJump(),
    )
){
    Scaffold(
        content = {
            LazyColumn(){
                items(list){ jump ->
                    JumpCard(jump = jump)
                }
            }
        },

        topBar = {
            AppTopAppBar(
                title = "Skyblu"
            )
        },
        bottomBar = {
            AppBottomAppBar(navController = rememberNavController( ))
        },
        
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {  Text(text = "Track Jump") },
                onClick = { },
                icon ={ Icon(painterResource(id = R.drawable.plane), contentDescription = "")}
            )
        }
    )
}