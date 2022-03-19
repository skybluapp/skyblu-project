package com.skyblu.userinterface.screens

import android.Manifest
import android.view.ViewDebug
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.generateSampleJump
import com.skyblu.models.jump.generateSampleJumpList
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.AppBottomAppBar
import com.skyblu.userinterface.componants.AppIcon
import com.skyblu.userinterface.componants.AppTopAppBar
import com.skyblu.userinterface.componants.JumpCard
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@Preview
@Composable
fun HomeScreen(
    list: List<Jump> = generateSampleJumpList(),
    navController : NavController = rememberNavController(),
    onFabClicked : () -> Unit = {}
){
    Scaffold(
        content = {
            LazyColumn(){
                items(list){ jump ->
                    JumpCard(
                        jump = jump,
                        onMapClick = { navController.navigate(AppIcon.Map.route) }
                    )
                }
            }
        },

        topBar = {
            AppTopAppBar(
                title = "Home",
                navigationIcon = null
            )
        },
        bottomBar = {
            AppBottomAppBar(navController = navController)
        },
        
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {  Text(text = "Track Jump") },
                onClick = {onFabClicked()},
                icon ={ Icon(painterResource(id = R.drawable.blue_plane), contentDescription = "")}
            )
        }
    )

}


