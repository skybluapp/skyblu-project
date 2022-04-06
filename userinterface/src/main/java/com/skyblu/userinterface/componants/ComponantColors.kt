package com.skyblu.userinterface.componants

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color





@Composable
fun sliderColors(): SliderColors {
    return SliderDefaults.colors(
        activeTickColor = MaterialTheme.colors.onBackground,
        thumbColor = MaterialTheme.colors.onBackground,
        activeTrackColor = MaterialTheme.colors.onBackground
    )
}