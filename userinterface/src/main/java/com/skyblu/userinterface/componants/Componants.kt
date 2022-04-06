package com.skyblu.userinterface.componants

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skyblu.models.jump.Skydive
import com.skyblu.models.jump.Skydiver
import com.skyblu.userinterface.R

object AppTextFieldDefaults {

    val borderWidth = 2.dp
    val height = TextFieldDefaults.MinHeight
}

@Preview(showBackground = true)
@Composable
fun AppButtonPreview() {
    AppButton(
        onClick = {},
        text = "Button",
        leadingIcon = R.drawable.blue_plane,
        trailingIcon = R.drawable.blue_plane
    )
}

@Composable
fun buttonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        disabledBackgroundColor = MaterialTheme.colors.background,
        disabledContentColor = MaterialTheme.colors.onBackground,
    )
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    text: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    colors: ButtonColors = buttonColors()
) {
    Button(
        onClick = { onClick() },
        colors = colors,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            Text(text = text)
            if (trailingIcon != null) {
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AppTextButton(
    onClick: () -> Unit,
    text: String = "",
    colors: ButtonColors = buttonColors()
) {
    TextButton(
        onClick = { onClick() },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun AppDisplayPhoto(
    size: Dp,
    image: ImageBitmap?,
    onClick: () -> Unit
) {
    if (image == null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://media.wired.com/photos/5b899992404e112d2df1e94e/master/pass/trash2-01.jpg")
                .crossfade(true)
                .build(),
            contentDescription = "barcode image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(size = size)
                .clip(CircleShape)
                .clickable { onClick() }
                .background(Color.LightGray),
        )
    } else {
        Image(
            bitmap = image,
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size = size)
                .clip(CircleShape)
                .clickable { onClick() }
                .background(Color.LightGray),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AppDisplayPhotoPreview() {
    val bitmap = BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.skydiver
    ).asImageBitmap()
    AppDisplayPhoto(
        size = 60.dp,
        image = bitmap,
        onClick = {}
    )
}

@Composable
fun AppTopAppBar(
    title: String,
    color: Color = MaterialTheme.colors.background,
    navigationIcon: @Composable (() -> Unit)? = null,
    actionIcons: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title) },
        backgroundColor = color,
        contentColor = MaterialTheme.colors.onBackground,
        actions = { Row { actionIcons() } },
        navigationIcon = navigationIcon
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewAppTopAppBar() {
    AppTopAppBar(
        title = "TopAppBar",
        navigationIcon = {
            MenuActionList(
                listOf(
                    ActionConcept(
                        concept = Concept.Previous,
                        action = {}),
                ),
            )
        },
        actionIcons = {
            MenuActionList(
                menuActions = listOf<ActionConcept>(
                    ActionConcept(
                        concept = Concept.Info,
                        action = {}),
                    ActionConcept(
                        concept = Concept.Person,
                        action = {}),
                    ActionConcept(
                        concept = Concept.Parachute,
                        action = {}),
                    ActionConcept(
                        concept = Concept.Key,
                        action = {}),
                )
            )
        },
    )
}

@Composable
fun RowScope.AddItem(
    screen: Concept,
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    BottomNavigationItem(
        label = { Text(text = screen.title) },
        icon = {
            Icon(
                painter = painterResource(id = screen.icon),
                contentDescription = screen.title
            )
        },
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    )
}

@Composable
fun MenuActionList(
    menuActions: List<ActionConcept>,
) {
    for (action in menuActions) {
        IconButton(onClick = { action.action() }) {
            Icon(
                painter = painterResource(id = action.concept.icon),
                contentDescription = null,
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun MenuActionList(
    appConcepts: List<Concept>,
    navController: NavController
) {
    for (action in appConcepts) {
        IconButton(onClick = { navController.navigate(action.route) }) {
            Icon(
                painter = painterResource(id = action.icon),
                contentDescription = null,
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun BasicIcon(list: List<Concept>) {
    for (action in list) {
        Icon(
            painter = painterResource(id = action.icon),
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
fun AppBottomAppBar(
    navController: NavController
) {
    val bottomNavIcons = listOf<Concept>(
        Concept.Home,
        Concept.Profile
    )
    BottomAppBar(
        cutoutShape = RoundedCornerShape(50),
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavIcons.forEach { bottomNavIcon ->
            var selected =
                currentDestination?.hierarchy?.any { it.route == bottomNavIcon.route } == true
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.route == bottomNavIcon.route } == true,
                onClick = {
                    navController.navigate(bottomNavIcon.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = bottomNavIcon.icon),
                        contentDescription = bottomNavIcon.title,
                        tint = MaterialTheme.colors.onBackground,
                    )
                },
                label = {
                    Text(
                        text = bottomNavIcon.title,
                        color = if (selected) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.onBackground
                        }
                    )
                },
                modifier = Modifier.background(MaterialTheme.colors.background)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewAppBottomAppBar(
    navController: NavController = rememberNavController()
) {
    AppBottomAppBar(navController = navController)
}

@Composable
@Preview(showBackground = true)
fun AppBannerPreview() {
    AppBanner(
        text = "This is a Banner",
        actionConcept = ActionConcept(
            action = {},
            concept = Concept.Close
        ),
        color = MaterialTheme.colors.background
    )
}

@Composable
fun AppJumpCardHeader(
    skydive: Skydive,
    username: String,
    skydiver : Skydiver
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.padding(4.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(skydiver.skydiverPhotoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "barcode image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(45.dp).clip(CircleShape)

            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = username,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
            Text(text = "Skydive Langar . 11/04/2022")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppDataPoint(
    appConcepts: Concept = Concept.AirPressure,
    data: String = "data"
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.fillMaxWidth(0.5f)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicIcon(list = listOf(appConcepts))
                Text(
                    text = appConcepts.title + ":",
                    Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column() {
            Text(
                text = data,
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppDataPoint2(
    appConcepts: Concept = Concept.Longitude,
    data: String = "data"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            BasicIcon(list = listOf(appConcepts))
        }
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = appConcepts.title + ":",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = data,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview
fun AppSettingsCategory(
    menuAction: ActionConcept = ActionConcept(
        action = {},
        concept = Concept.LocationTracking
    )
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colors.background)
            .clickable { menuAction.action() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = menuAction.concept.icon),
            contentDescription = menuAction.concept.title,
            Modifier.padding(
                start = 12.dp,
                end = 12.dp
            )
        )


        Text(
            text = menuAction.concept.title,
        )
    }
}

@Composable
fun AppBanner(
    text: String,
    actionConcept: ActionConcept,
    color: Color
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(color = color),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            Modifier
                .fillMaxWidth(0.9f)
                .padding(start = 8.dp),
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = { actionConcept.action() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun AppSettingsSlider(
    appConcepts: Concept = Concept.Plane,
    title: String? = "Slider",
    value: Float,
    onValueChanged: (Float) -> Unit = {},
    range: ClosedFloatingPointRange<Float>,
    prepend: String = ""
) {
    Column() {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.Bottom,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 12.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = appConcepts.icon),
                    contentDescription = appConcepts.title
                )
                Text(
                    text = title ?: appConcepts.title,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Slider(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(0.8f),
                value = value,
                onValueChange = { onValueChanged(it) },
                colors = sliderColors(),
                valueRange = range,
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    value.toInt().toString() + prepend
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppSettingsRangeSlider(
    appConcepts: Concept = Concept.Plane,
    title: String? = "Slider",
    values: ClosedFloatingPointRange<Float>,
    onValueChanged: (ClosedFloatingPointRange<Float>) -> Unit = {},
    range: ClosedFloatingPointRange<Float>,
    prepend: String = ""
) {
    Column() {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.Bottom,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 12.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = appConcepts.icon),
                    contentDescription = appConcepts.title
                )
                Text(
                    text = title ?: appConcepts.title,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }




        Row(
            Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(0.5f)
            ) {
                Text(
                    values.start.toInt().toString() + prepend
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(2f)
            ) {
                RangeSlider(
                    values = values,
                    onValueChange = { onValueChanged(it) },
                    valueRange = range,
                    colors = sliderColors()
                )
            }




            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(0.5f)
            ) {
                Text(
                    values.endInclusive.toInt().toString() + prepend
                )
            }
        }
    }
}

@Preview
@Composable
fun AppSliderPreview() {
    var sliderPosition by remember { mutableStateOf(0f) }
    AppSettingsSlider(
        value = sliderPosition,
        onValueChanged = { sliderPosition = it },
        range = 0f .. 10f
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun AppRangeSliderPreview() {
    var sliderPosition by remember { mutableStateOf(30f .. 100f) }
    var sliderPosition2 by remember { mutableStateOf(30f .. 100f) }


    RangeSlider(
        values = sliderPosition,
        onValueChange = { sliderPosition = it },
        valueRange = 0f .. 150f
    )


    AppSettingsRangeSlider(
        values = sliderPosition2,
        onValueChanged = { sliderPosition2 = it },
        range = 0f .. 150f
    )
}




