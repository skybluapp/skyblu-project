package com.skyblu.uicomponants

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.Navigation

import com.skyblu.uicomponants.ui.theme.UiComponantsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UiComponantsTheme {
                Greeting()
            }
        }
    }
}
@Composable
fun Greeting() {
    var value by remember{ mutableStateOf("") }
//    AppTextField(value = value, onValueChanged = {value = it},placeholder = "placeholder", leadingIcon = R.drawable.plane, trailingIcon = R.drawable.plane, imeAction = ImeAction.Search)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UiComponantsTheme {
        Greeting()
    }
}