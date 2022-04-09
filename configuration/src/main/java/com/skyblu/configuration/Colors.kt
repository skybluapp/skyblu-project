package com.skyblu.configuration

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color



val WALKING_COLOR = Color(0xFF000000)
val AIRCRAFT_COLOR = Color(0xFF53E2F1)
val FREEFALL_COLOR = Color(0xFF2644FF)
val CANOPY_COLOR = Color(0xFFE64A19)
val LANDED_COLOR = Color(0xFF000000)

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val ThemeBlueOne = Color(0xFF00A8FF)
val ThemeBlueTwo = Color(0xFF00A8FF)

val ThemeBlueGradient = Brush.verticalGradient(
    listOf<Color>(ThemeBlueOne, ThemeBlueTwo)
)

val DarkColorPalette = darkColors(
    primary = ThemeBlueOne,
    primaryVariant = Purple700,
    secondary = ThemeBlueTwo,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.DarkGray
)

val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.LightGray
)