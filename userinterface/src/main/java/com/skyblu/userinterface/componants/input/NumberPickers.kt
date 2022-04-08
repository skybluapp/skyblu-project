package com.skyblu.userinterface.componants.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.*


@Composable
fun StyledNumberPicker(
    value : Int,
    onValueChanged : (Int) -> Unit,
    range : IntRange
){
    NumberPicker(
        value = value,
        range = range,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = Modifier.fillMaxWidth()
    )
}