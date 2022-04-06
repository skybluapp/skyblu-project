package com.skyblu.userinterface.componants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyblu.models.jump.Skydive
import com.skyblu.models.jump.Skydiver
import com.skyblu.models.jump.generateSampleJump
import timber.log.Timber


@Composable
fun JumpCard(
    skydive: Skydive = generateSampleJump(),
    onMapClick : () -> Unit = {},
    username : String = "Meep Moop",
    skydiver : Skydiver = Skydiver()
){

    Timber.d("ComposeJumpCard")

    Timber.d("URL" + skydive.staticMapUrl)

    Column {
        AppJumpCardHeader(skydive = skydive, username = username, skydiver = skydiver)
        Box(modifier = Modifier
            .height(350.dp)
            .fillMaxWidth()){
            StaticGoogleMap(skydive = skydive, onClick = { onMapClick() })
        }

    }
}