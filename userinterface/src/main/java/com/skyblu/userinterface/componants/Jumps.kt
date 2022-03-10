package com.skyblu.userinterface.componants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.generateSampleJump

@Preview
@Composable
fun JumpCard(jump: Jump = generateSampleJump()){
    Column {
        AppJumpCardHeader()
        Box(modifier = Modifier
            .height(350.dp)
            .fillMaxWidth()){
            StaticGoogleMap()
        }

    }
}