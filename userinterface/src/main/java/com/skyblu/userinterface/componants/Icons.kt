package com.skyblu.userinterface.componants

import androidx.annotation.DrawableRes
import com.skyblu.userinterface.R

data class MenuAction(
    val onClick: () -> Unit,
    val menuIcon: MenuIcon
)

sealed class BottomNavIcon(
    val title: String,
    @DrawableRes val icon : Int,
    val route : String
){
    object Home : BottomNavIcon(
        title = "Home",
        icon = R.drawable.home,
        route = "home"
    )
    object Profile : BottomNavIcon(
        title = "Profile",
        icon = R.drawable.person,
        route = "profile"
    )
}

sealed class MenuIcon(
    val title : String,
    @DrawableRes val icon: Int,
    ) {
    object Add : MenuIcon("Add" ,
        R.drawable.add)
    object AddPhoto : MenuIcon("Add Photo" ,
        R.drawable.add_photo)
    object AirPressure : MenuIcon("Air Pressure" ,
        R.drawable.air)
    object Award : MenuIcon("Awards" ,
        R.drawable.award)
    object Ground : MenuIcon("Ground" ,
        R.drawable.bottom)
    object Time : MenuIcon("Time" ,
        R.drawable.clock)
    object Edit : MenuIcon("Edit" ,
        R.drawable.edit)
    object Email : MenuIcon("Email" ,
        R.drawable.email)
    object Group : MenuIcon("Group" ,
        R.drawable.group)
    object Height : MenuIcon("Height" ,
        R.drawable.height)
    object Help : MenuIcon("Help" ,
        R.drawable.help)
    object Info : MenuIcon("Info" ,
        R.drawable.info)
    object Key : MenuIcon("Key" ,
        R.drawable.key)
    object Location : MenuIcon("Location" ,
        R.drawable.location)
    object LocationNotTracking : MenuIcon("Not Tracking" ,
        R.drawable.location_not_tracking)
    object LocationTracking : MenuIcon("Tracking" ,
        R.drawable.location_tracking)
    object More : MenuIcon("More" ,
        R.drawable.more)
    object Next : MenuIcon("Next" ,
        R.drawable.next)
    object Parachute : MenuIcon("Parachute" ,
        R.drawable.parachute)
    object Password : MenuIcon("Password" ,
        R.drawable.password)
    object Person : MenuIcon("Person" ,
        R.drawable.person)
    object Photo : MenuIcon("Photo" ,
        R.drawable.photo)
    object Plane : MenuIcon("Aircraft" ,
        R.drawable.plane)
    object Previous : MenuIcon("Previous" ,
        R.drawable.previous)
    object Save : MenuIcon("Save" ,
        R.drawable.save)
    object Send : MenuIcon("Send" ,
        R.drawable.send)
    object Sensor : MenuIcon("Sensor" ,
        R.drawable.sensor)
    object Share : MenuIcon("Share" ,
        R.drawable.share)
    object Star : MenuIcon("Star" ,
        R.drawable.star)
    object Tag : MenuIcon("Tag" ,
        R.drawable.tag)
    object Up : MenuIcon("Up" ,
        R.drawable.up)
    object Logout : MenuIcon("Logout" ,
        R.drawable.logout)
    object Login : MenuIcon("Login" ,
        R.drawable.login)
    object Home : MenuIcon("Home" ,
        R.drawable.home)
    object Close : MenuIcon( "Close",
        R.drawable.close
    )
}