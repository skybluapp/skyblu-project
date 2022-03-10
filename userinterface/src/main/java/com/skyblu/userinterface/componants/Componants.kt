package com.skyblu.userinterface.componants

import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skyblu.userinterface.R

@Preview(showBackground = true)
@Composable
fun AppTextFieldPreview() {
    var value by remember { mutableStateOf("") }
    AppTextField(
        value = value,
        onValueChanged = { value = it },
        placeholder = "TextField",
        leadingIcon = R.drawable.plane,
        trailingIcon = R.drawable.plane
    )
}
@Composable
fun AppTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    onIme: () -> Unit = {},
    placeholder: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default
) {
    val colors = MaterialTheme.colors
    val keyboardOptions = KeyboardOptions(
        keyboardType = keyboardType,
        imeAction = imeAction,
    )
    val keyboardActions = KeyboardActions(
        onNext = { onIme() },
        onGo = { onIme() },
        onSearch = { onIme() },
        onDone = { onIme() }
    )
    TextField(
        value = value,
        onValueChange = { onValueChanged(it) },
        colors = textFieldColors(),
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = "",
                    tint = colors.onBackground
                )
            }
        },
        trailingIcon = {
            if (trailingIcon != null) {
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = "",
                    tint = colors.onBackground
                )
            }
        },
        placeholder = { Text(text = placeholder) },
        maxLines = maxLines,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    AppTextFieldDefaults.borderWidth,
                    color = colors.onBackground
                ),
                shape = CircleShape
            )
            .heightIn(
                AppTextFieldDefaults.height,
                AppTextFieldDefaults.height
            ),
        keyboardActions = keyboardActions
    )
}
@Composable
fun textFieldColors(): TextFieldColors {
    return TextFieldDefaults.textFieldColors(
        textColor = MaterialTheme.colors.onBackground,
        disabledTextColor = Color.Black,
        backgroundColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        placeholderColor = MaterialTheme.colors.onBackground
    )
}

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
        leadingIcon = R.drawable.plane,
        trailingIcon = R.drawable.plane
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
) {
    Button(
        onClick = { onClick() },
        Modifier.background(MaterialTheme.colors.background),
        colors = buttonColors(),
        border = BorderStroke(
            AppTextFieldDefaults.borderWidth,
            MaterialTheme.colors.onBackground
        ),
        shape = CircleShape
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
fun AppDisplayPhoto(
    size: Dp,
    image : ImageBitmap,
    onClick: () -> Unit
) {
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
@Composable
@Preview(showBackground = true)
fun AppDisplayPhotoPreview() {
    val bitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.skydiver).asImageBitmap()
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
    navigationIcon: @Composable () -> Unit = {},
    actionIcons: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = Modifier.background(color = Color.Gray),
        backgroundColor = color,
        contentColor = MaterialTheme.colors.onBackground,
        actions = { Row { actionIcons() } }
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
                    MenuAction(
                        menuIcon = MenuIcon.Previous,
                        onClick = {}),
                ),
            )
        },
        actionIcons = {
            MenuActionList(
                menuActions = listOf<MenuAction>(
                    MenuAction(
                        menuIcon = MenuIcon.Info,
                        onClick = {}),
                    MenuAction(
                        menuIcon = MenuIcon.Person,
                        onClick = {}),
                    MenuAction(
                        menuIcon = MenuIcon.Parachute,
                        onClick = {}),
                    MenuAction(
                        menuIcon = MenuIcon.Key,
                        onClick = {}),
                )
            )
        },
    )
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavIcon,
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
    menuActions: List<MenuAction>
) {
    for (action in menuActions) {
        IconButton(onClick = { action.onClick() }) {
            Icon(
                painter = painterResource(id = action.menuIcon.icon),
                contentDescription = null,
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}
@Composable
fun AppBottomAppBar(
    navController: NavController
) {
    val bottomNavIcons = listOf<BottomNavIcon>(
        BottomNavIcon.Home,
        BottomNavIcon.Profile
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavIcons.forEach { bottomNavIcon ->
            var selected = currentDestination?.hierarchy?.any { it.route == bottomNavIcon.route } == true
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
                    Text(text = bottomNavIcon.title, color = if(selected){MaterialTheme.colors.primary}else{MaterialTheme.colors.onBackground})
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
fun AppBanner(
    text : String,
    menuAction: MenuAction,
    color: Color
){
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically,

    ){
        Text(
            text = "This is a Banner",
            Modifier
                .fillMaxWidth(0.9f)
                .padding(start = 8.dp),
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = ""
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBannerPreview(){
    AppBanner(
        text = "This is a Banner",
        menuAction = MenuAction(onClick = {}, menuIcon = MenuIcon.Close),
        color = MaterialTheme.colors.background
    )
}

@Composable
fun AppJumpCard(){
    Column {
        AppJumpCardHeader()
        Box(Modifier.height(350.dp)){
            JumpMap()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppJumpCardHeader(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.padding(4.dp)) {
            AppDisplayPhoto(
                size = 60.dp,
                image = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.skydiver).asImageBitmap(),
                onClick = {},
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text =  "0listocks", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h6)
            Text(text = "Skydive Langar . 11/04/2022")
        }
    }
}




